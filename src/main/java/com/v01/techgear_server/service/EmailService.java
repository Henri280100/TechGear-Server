package com.v01.techgear_server.service;


import com.v01.techgear_server.dto.UserDTO;

public interface EmailService {
    void sendVerificationEmail(UserDTO user);

    String generateEmailBody(String verificationUrl, Long userId);
    // void sendVerificationEmail(EmailDTO email);

    void verifyEmail(String urlToken);

    void resendVerificationEmail(String emailAddress);

}
