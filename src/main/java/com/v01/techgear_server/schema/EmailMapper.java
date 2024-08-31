package com.v01.techgear_server.schema;

import com.v01.techgear_server.generated.EmailSchema;
import com.v01.techgear_server.model.Email;

public class EmailMapper {
    public static EmailSchema mapToEmailSchema(Email email) {
        return EmailSchema.newBuilder()
            .setEmailId(email.getEmailId())
            .setEmailAddress(email.getEmailAddress())
            .setVerificationToken(email.getVerificationToken())
            .setVerified(email.isVerified())
            .setSentAt(email.getSentAt().toString())
            .setVerifiedAt(email.getVerifiedAt() != null ? email.getVerifiedAt().toString() : null)
            .setRecipient(email.getRecipient())
            .setMsgBody(email.getMsgBody())
            .setSubject(email.getSubject())
            .setAttachment(email.getAttachment())
            .setUser(UserMapper.mapToUserSchema(email.getUser())) // Link to UserSchema
            .build();
    }
}
