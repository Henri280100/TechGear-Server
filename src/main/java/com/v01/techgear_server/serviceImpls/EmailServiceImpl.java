package com.v01.techgear_server.serviceImpls;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.v01.techgear_server.security.TokenGenerator;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.dto.TokenDTO;
import com.v01.techgear_server.model.ConfirmationTokens;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.repo.jpa.ConfirmationTokensRepository;
import com.v01.techgear_server.repo.jpa.UserRepository;
import com.v01.techgear_server.service.EmailService;
import com.v01.techgear_server.service.RateLimiterService;
import com.v01.techgear_server.utils.EncryptionUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final TokenGenerator tokenGenerator;
    private final RateLimiterService rateLimiterService;
    private final UserRepository userRepository;
    private final ConfirmationTokensRepository confirmationTokensRepository;
    private final JavaMailSender mailSender;

    public EmailServiceImpl(@Lazy TokenGenerator tokenGenerator, RateLimiterService rateLimiterService, UserRepository userRepository, ConfirmationTokensRepository confirmationTokensRepository, JavaMailSender mailSender) {
        this.tokenGenerator = tokenGenerator;
        this.rateLimiterService = rateLimiterService;
        this.userRepository = userRepository;
        this.confirmationTokensRepository = confirmationTokensRepository;
        this.mailSender = mailSender;
    }


    @Override
    public void sendVerificationEmail(User user) {
        String email = user.getEmail();
        if (rateLimitExceeded(email)) {
            log.error("Rate limit exceeded for sending verification emails to: {}", email);
            return;
        }

        if (rateLimiterService.hasEmailBeenSent(email, 10000)) {
            log.info("Email has already been sent to: {}", email);
            return;
        }

        ConfirmationTokens confirmToken = generateAndSaveToken(user);
        String verificationUrl = buildVerificationUrl(confirmToken.getConfirmToken());

        String emailBody = generateEmailBody(verificationUrl, user.getUsername());
        String encryptedBody = EncryptionUtil.encrypt(emailBody);

        CompletableFuture.allOf(
                sendEmailAsync(user.getEmail(), "Verify your email", encryptedBody),
                rateLimiterService.recordEmailSentAsync(email))
                .thenRun(() -> log.info("Email sent and recorded successfully for: {}", email))
                .exceptionally(ex -> {
                    log.error("Error while sending verification email: {}", ex.getMessage(), ex);
                    return null;
                });
    }

    private CompletableFuture<Void> sendEmailAsync(String to, String subject, String body) {
        return CompletableFuture.runAsync(() -> sendEmail(to, subject, body));
    }

    @Override
    public void sendResetTokenEmail(String email, String resetToken) {
        if (rateLimitExceeded(email) || rateLimiterService.hasEmailBeenSent(email, 10000)) {
            log.error("Rate limit exceeded or email already sent for: {}", email);
            return;
        }

        sendEmailAsync(email, "Reset password", 
            "To reset your password, click the link below:\n" +
            "http://localhost:8082/api/v01/auth/reset-password?token=" + resetToken)
            .thenRun(() -> rateLimiterService.recordEmailSentAsync(email))
            .exceptionally(ex -> {
                log.error("Error sending reset password email: {}", ex.getMessage(), ex);
                return null;
            });
    }

    @Override
    public String verifyEmail(String token) {
        return confirmationTokensRepository.findByConfirmToken(token)
                .map(confirmToken -> {
                    if (confirmToken.getConfirmedAt() != null) {
                        log.error("Token already confirmed");
                        return "Email is already confirmed!";
                    }

                    LocalDateTime now = LocalDateTime.now();
                    if (confirmToken.getExpiryDate().isBefore(now)) {
                        log.error("Token expired");
                        return "Token expired!";
                    }

                    User user = confirmToken.getUsers();
                    if (!user.isActive()) {
                        user.setActive(true);
                        userRepository.save(user);
                    }

                    confirmToken.setConfirmedAt(now);
                    confirmationTokensRepository.save(confirmToken);

                    TokenDTO userToken = tokenGenerator.generateTokens(user);
                    log.info("Email confirmed for user: {}", user.getUsername());

                    return userToken.getAccessToken();
                })
                .orElse("ERROR: cannot verify email");
    }

    @Override
    public void resendVerificationEmail(String email) {
        userRepository.findByEmail(email)
                .filter(user -> !user.isActive())
                .ifPresent(this::sendVerificationEmail);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("phamk883@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
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
            log.error("Rate limit exceeded for email: {}", email);
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
