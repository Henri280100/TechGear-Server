package com.v01.techgear_server.serviceImpls.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.generated.EmailSchema;
import com.v01.techgear_server.model.Email;
import com.v01.techgear_server.repo.EmailRepository;

@Service
public class EmailConsumerService {
    
    @Autowired
    private EmailRepository emailRepository;

    private static Logger LOGGER = LoggerFactory.getLogger(EmailConsumerService.class);

    @KafkaListener(topics = "email-topic", groupId = "email-group")
    public void listenEmailTopic(EmailSchema emailSchema) {
        try {
            LOGGER.info("Received email schema: {}", emailSchema);

            // Process the email schema object
            Email email = mapToEmail(emailSchema);
            emailRepository.save(email);

        } catch (Exception e) {
            LOGGER.error("Error occurred while processing email schema: {}", e.getMessage(), e);
        }
    }

    private Email mapToEmail(EmailSchema emailSchema) {
        // Implement the mapping from EmailSchema to Email entity
        Email email = new Email();
        email.setEmailId(emailSchema.getEmailId());
        return email;
    }
}
