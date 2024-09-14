package com.v01.techgear_server.serviceImpls;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.model.ConfirmationTokens;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.ConfirmationTokensRepository;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.security.TokenGenerator;
import com.v01.techgear_server.service.EmailService;
import com.v01.techgear_server.service.RateLimiterService;
import com.v01.techgear_server.utils.EncryptionUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    private OnStoreCloudDBServiceImpl onStoreCloud;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokensRepository confirmationTokensRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendVerificationEmail(User user) {
        try {
            String email = user.getEmail();
            long timeWindowMillis = 10000;
            if (rateLimitExceeded(email)) {
                LOGGER.error("Rate limit exceeded for sending verification emails to: ", email);
                return;
            }

            if (rateLimiterService.hasEmailBeenSent(email, timeWindowMillis)) {
                LOGGER.info("Email has already been sent to: ", email);
                return;
            }

            // User user = modelMapper.map(userDTO, User.class);
            ConfirmationTokens confirmToken = generateAndSaveToken(user);
            String verificationUrl = buildVerificationUrl(confirmToken.getConfirmToken());

            String emailBody = generateEmailBody(verificationUrl, user.getUsername());
            String encryptedBody = EncryptionUtil.encrypt(emailBody);

            CompletableFuture<Void> firebaseStoreFuture = onStoreCloud.storeData("emails",
                    "email-verification-" + user.getId(), encryptedBody);

            sendEmail(user.getEmail(), "Verify your email", emailBody);

            firebaseStoreFuture.thenRun(() -> rateLimiterService.recordEmailSent(email))
                    .exceptionally(ex -> {
                        LOGGER.error("Error storing email body in Firebase: {}", ex.getMessage(), ex);
                        return null;
                    });

        } catch (Exception e) {
            LOGGER.error("Error while sending verification email: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendResetTokenEmail(String email, String resetToken) {
        User user = new User();
        user.setEmail(email);
        long timeWindowMillis = 10000;
        if (rateLimitExceeded(email)) {
            LOGGER.error("Rate limit exceeded for sending reset password emails to: ", email);
            return;
        }

        if (rateLimiterService.hasEmailBeenSent(email, timeWindowMillis)) {
            LOGGER.info("Email has already been sent to: ", email);
            return;
        }

        String subject = "Reset password";
        String emailText = "To reset your password, click the link below:\n" +
                "http://localhost:8082/api/v01/auth/reset-password?token=" + resetToken;
        sendEmail(email, subject, emailText);
    }

    @Override
    public String verifyEmail(String token) {
        try {
            ConfirmationTokens confirmToken = confirmationTokensRepository.findByConfirmToken(token)
                    .orElseThrow(() -> new IllegalStateException("Token not found"));

            if (confirmToken.getConfirmedAt() != null) {
                LOGGER.error("Token already confirmed");
                return "Email is already confirmed!";
            }

            if (confirmToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                LOGGER.error("Token expired");
                return "Token expired!";
            }

            User user = confirmToken.getUsers();
            if (!user.isActive()) {
                user.setActive(true);
                userRepository.save(user);
            }

            confirmToken.setConfirmedAt(LocalDateTime.now());
            confirmationTokensRepository.save(confirmToken);

            // Generate authentication token after email verification
            Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                    user, user.getPassword(), user.getAuthorities());

            tokenGenerator.createToken(authentication);

            LOGGER.info("Email confirmed for user: {}", user.getUsername());
            return "Email confirmed successfully!, please return to the login page";
        } catch (Exception e) {
            LOGGER.error("Cannot verify email please check again", e);
            return "ERROR: cannot verify email";
        }
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (user.isActive()) {
            LOGGER.error("User already confirmed");
            throw new IllegalStateException("User already confirmed");
        }

        // UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        sendVerificationEmail(user);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email to: {}", to, e);
            throw new IllegalStateException("Failed to send email");
        }
    }

    @Override
    public String generateEmailBody(String verificationUrl, String username) {
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
                + "<p>Hello, " + username + ".</p>"
                + "<p>Thank you for registering with us. Please click the button below to verify your email address:</p>"
                + "<a href=\"" + verificationUrl + "\" target=\"_blank\">Verify Email</a>"
                + "<p>If you did not create an account, no further action is required.</p>"
                + "</div>"
                + "<div class=\"footer\">"
                + "<p>&copy; 2024 TechGear. All rights reserved.</p>"
                + "</div>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    private boolean rateLimitExceeded(String email) {
        String rateLimitKey = "sendVerificationEmail:" + email;
        if (rateLimiterService.isRateLimited(rateLimitKey)) {
            LOGGER.error("Rate limit exceeded for email: {}", email);
            return true;
        }
        return false;
    }

    private ConfirmationTokens generateAndSaveToken(User user) {
        String verificationToken = UUID.randomUUID().toString();
        ConfirmationTokens confirmToken = new ConfirmationTokens();
        confirmToken.setConfirmToken(verificationToken);
        confirmToken.setCreatedDate(LocalDateTime.now());
        confirmToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        confirmToken.setUsers(user);
        return confirmationTokensRepository.save(confirmToken);
    }

    private String buildVerificationUrl(String token) {
        return "http://localhost:8082/api/v01/auth/verify-email?token=" + token;
    }

}
