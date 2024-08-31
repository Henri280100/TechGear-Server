package com.v01.techgear_server.serviceImpls.Email;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.dto.EmailDTO;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.exception.CustomRateLimiterException;
import com.v01.techgear_server.generated.EmailSchema;
import com.v01.techgear_server.model.Email;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.EmailRepository;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.schema.EmailMapper;
import com.v01.techgear_server.service.EmailService;
import com.v01.techgear_server.service.RateLimiterService;
import com.v01.techgear_server.serviceImpls.AzureStorageServiceImpl;
import com.v01.techgear_server.utils.EncryptionUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private KafkaTemplate<String, EmailSchema> kafkaTemplate;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AzureStorageServiceImpl azureStorageService;

    private static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);


    /**
     * Sends a verification email to the user's email address(es) specified in the UserDTO.
     * The email is sent asynchronously and will be rate limited to prevent abuse.
     * The email body is encrypted and uploaded to Azure blob storage, and the blob URL is stored in the database.
     * The email is also published to a Kafka topic.
     * @param userDTO The user DTO containing the user's email address(es)
     */
    @Async
    @Override
    public void sendVerificationEmail(UserDTO userDTO) {
        List<EmailDTO> emailDTOs = userDTO.getEmail();
        long timeWindowMillis = 24 * 60 * 60 * 1000; // Example: 24 hours

        for (EmailDTO emailDTO : emailDTOs) {
            String emailAddress = emailDTO.getEmailAddress();
            String rateLimitKey = "sendVerificationEmail:" + emailAddress;

            try {
                // Check if an email has already been sent within the time window
                if (rateLimiterService.hasEmailBeenSent(emailAddress, timeWindowMillis)) {
                    LOGGER.warn("An email has already been sent to {} within the last 24 hours.", emailAddress);
                    continue; // Skip to the next email
                }

                // Rate limiting check
                if (rateLimiterService.isRateLimited(rateLimitKey)) {
                    LOGGER.warn("Rate limit exceeded for user: {}", emailAddress);
                    continue; // Skip to the next email
                }

                Email email = modelMapper.map(userDTO, Email.class);
                User user = modelMapper.map(userDTO, User.class);

                // Populate email schema
                EmailSchema emailSchema = EmailMapper.mapToEmailSchema(email);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);

                String urlToken = UUID.randomUUID().toString();

                String verificationUrl = "http://localhost:8080/api/auth/verify-email?token=" + urlToken;

                String emailBody = generateEmailBody(verificationUrl, user.getUserId());
                String emailFileName = "email-verification-" + user.getUserId() + ".html";
                String encryptedEmailBody = EncryptionUtil.encrypt(emailBody);
                String blobUrl = azureStorageService.uploadEmailBody(encryptedEmailBody, emailFileName);

                email.setEmailAddress(user.getUsername());
                email.setVerificationToken(verificationUrl);
                email.setSentAt(LocalDateTime.now());
                email.setRecipient(user.getUsername());

                email.setMsgBody(blobUrl); // Store the blob URL in the database instead of the actual email body
                email.setUser(user);
                email.setSubject("Email verification");

                helper.setTo(user.getUsername());
                helper.setSubject(email.getSubject());
                helper.setText(email.getMsgBody(), true);
                mailSender.send(message);

                emailRepository.save(email);

                // call Kafka producer
                kafkaTemplate.send("email-topic", emailSchema.getEmailAddress(), emailSchema);

                // Record that an email was sent
                rateLimiterService.recordEmailSent(emailAddress);

                LOGGER.info("Email sent successfully to {}", emailAddress);

            } catch (MessagingException e) {
                LOGGER.error("Exception occurred while sending verification email to {}: {}", emailAddress,
                        e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.error("Exception occurred while processing email for {}: {}", emailAddress, e.getMessage(), e);
            }
        }
    }

    

    /**
     * Verify an email address by the given verification token.
     *
     * @param urlToken the verification token
     * @return a ResponseEntity indicating whether the verification was successful
     * @throws Exception if an error occurred while verifying the email
     */
    @Transactional
    @Override
    public void verifyEmail(String urlToken) {
        Email email = emailRepository.findByVerificationToken(urlToken);
        try {

            if (email == null) {
                LOGGER.warn("Invalid verification token: {}", urlToken);
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification token.");
                return;
            }

            if (email.isVerified()) {
                LOGGER.warn("Email already verified: {}", email.getEmailAddress());
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already verified.");
                return;
            }

            if (email.getSentAt().plusHours(24).isBefore(LocalDateTime.now())) {
                LOGGER.warn("Verification token expired for email: {}", email.getEmailAddress());
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification token has expired.");
                return;
            }

            // Mark the email as verified
            email.setVerified(true);
            email.setVerifiedAt(LocalDateTime.now());
            emailRepository.save(email);

            // Activate the associated user
            User user = email.getUser();
            if (user != null) {
                user.setActive(true);
                userRepository.save(user);
                LOGGER.info("User activated: {}", user.getUsername());
            }

            LOGGER.info("Email verified successfully for: {}", email.getEmailAddress());
            ResponseEntity.status(HttpStatus.OK).body("Email verified successfully. Your account is now active.");
        } catch (Exception e) {
            LOGGER.error("Exception occurred while verifying email: {}", e.getMessage(), e);
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while verifying your email.");
        }
    }

    /**
     * Resends a verification email to a user who has not yet verified their email
     * address. The email address is used to look up the associated email record in
     * the database. If the email record is not found, an exception is thrown. If
     * the
     * email is already verified, an exception is thrown. If the rate limit for the
     * user has been exceeded, an exception is thrown. If any other exception
     * occurs,
     * an error message is logged. The email record is updated with a new token and
     * the email is sent to the user. The email is then published to Kafka and the
     * rate limit is recorded. If the email is successfully sent, a success message
     * is
     * logged.
     * 
     * @param emailAddress The email address of the user to resend the verification
     *                     email to.
     */
    @Async
    @Override
    public void resendVerificationEmail(String emailAddress) {
        long timeWindowMillis = 24 * 60 * 60 * 1000; // Example: 24 hours
        String rateLimitKey = "resendVerificationEmail:" + emailAddress;

        try {
            // Retrieve the email record
            Email email = emailRepository.findByEmailAddress(emailAddress);

            if (email == null) {
                LOGGER.warn("No email found for address: {}", emailAddress);
                throw new IllegalArgumentException("Email address not found.");
            }

            // Check if the email is already verified
            if (email.isVerified()) {
                LOGGER.warn("Email already verified: {}", emailAddress);
                throw new IllegalStateException("Email already verified.");
            }

            if (email.getSentAt() != null &&
                    Duration.between(email.getSentAt(), LocalDateTime.now()).toMillis() < timeWindowMillis) {
                LOGGER.warn("An email has already been sent to {} within the last 24 hours.", emailAddress);
                throw new CustomRateLimiterException("Email has already been sent within the time window.");
            }

            // Rate limiting check
            if (rateLimiterService.isRateLimited(rateLimitKey)) {
                LOGGER.warn("Rate limit exceeded for user: {}", emailAddress);
                throw new CustomRateLimiterException("Rate limit exceeded. Please try again later.");
            }

            // Generate new token
            String newToken = UUID.randomUUID().toString();
            String verificationUrl = "http://localhost:8080/api/auth/verify-email?token=" + newToken;

            // Update the email record with the new token
            email.setVerificationToken(verificationUrl);
            email.setSentAt(LocalDateTime.now());
            email.setVerified(false);

            emailRepository.save(email);

            // Create email schema and send email
            // EmailSchema emailSchema = EmailMapper.mapToEmailSchema(email);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            String emailBody = generateEmailBody(verificationUrl, email.getUser().getUserId());
            String emailFileName = "email-verification-" + email.getUser().getUserId() + ".html";
            String encryptedEmailBody = EncryptionUtil.encrypt(emailBody);
            String blobUrl = azureStorageService.uploadEmailBody(encryptedEmailBody, emailFileName);

            email.setMsgBody(blobUrl);

            helper.setTo(email.getRecipient());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMsgBody(), true);
            mailSender.send(message);

            // Publish to Kafka
            // kafkaTemplate.send("email-topic", emailSchema);

            // Record that an email was sent
            rateLimiterService.recordEmailSent(emailAddress);

            LOGGER.info("Verification email resent successfully to {}", emailAddress);

        } catch (MessagingException e) {
            LOGGER.error("Exception occurred while resending verification email to {}: {}", emailAddress,
                    e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while processing email for {}: {}", emailAddress, e.getMessage(), e);
        }
    }

    @Override
    public String generateEmailBody(String verificationUrl, Long userId) {
        return "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>Email Verification</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }"
                + ".container { width: 100%; padding: 20px; background-color: #f4f4f4; }"
                + ".email-content { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                + ".header { text-align: center; background-color: #007bff; color: #ffffff; padding: 10px 0; border-radius: 5px 5px 0 0; }"
                + ".header h1 { margin: 0; font-size: 24px; }"
                + ".body { padding: 20px; text-align: center; }"
                + ".body p { font-size: 16px; color: #333333; line-height: 1.5; }"
                + ".body a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 5px; }"
                + ".footer { text-align: center; margin-top: 20px; color: #777777; font-size: 12px; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"container\">"
                + "<div class=\"email-content\">"
                + "<div class=\"header\">"
                + "<h1>Email Verification</h1>"
                + "</div>"
                + "<div class=\"body\">"
                + "<p>Hello,</p>"
                + "<p>Thank you for registering with us. Please click the button below to verify your email address:</p>"
                + "<a href=\"" + verificationUrl + "\" target=\"_blank\">Verify Email</a>"
                + "<p>If you did not create an account, no further action is required.</p>"
                + "</div>"
                + "<div class=\"footer\">"
                + "<p>&copy; 2024 MyApp. All rights reserved.</p>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

}
