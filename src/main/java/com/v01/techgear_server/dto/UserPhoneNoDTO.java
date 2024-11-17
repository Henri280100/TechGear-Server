package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.CommunicationPreference;
import com.v01.techgear_server.enums.PhoneNumberPurpose;
import com.v01.techgear_server.enums.PhoneNumberType;
import com.v01.techgear_server.model.UserPhoneNo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPhoneNoDTO {
    
    @Schema(description = "Unique user Phone Number ID", example = "123")
    private Long id;

    @Schema(description = "Phone number", example = "0905339064")
    private String phoneNo;

    @Schema(description="Country code", example = "+84")
    private String countryCode;

    @Schema(description = "User associated with the phone number")
    private UserDTO user;

    @Schema(description="Type of phone number", example="MOBILE")
    private PhoneNumberType type;

    @Schema(description="Phone number purpose", example="PERSONAL")
    private PhoneNumberPurpose purpose;

    @Schema(description = "Is Phone Number Verified")
    private boolean phoneNumberVerified;

    @Schema(description = "Is Primary Phone Number")
    private boolean primary;

    @Schema(description = "Marketing Consent Status")
    private boolean marketingConsent;

    @Schema(description = "Two Factor Authentication Enabled")
    private boolean twoFactorAuthEnabled;

    @Schema(description = "Preferred Communication Channel")
    private CommunicationPreference communicationPreference;

    @Schema(description = "Verification Timestamp")
    private LocalDateTime verifiedAt;

    @Schema(description = "Creation Timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last Update Timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Marketing Consent Timestamp")
    private LocalDateTime marketingConsentAt;

    public static UserPhoneNoDTO fromEntity(UserPhoneNo entity) {
        if (entity == null) {
            return null;
        }
    
        return UserPhoneNoDTO.builder()
            .id(entity.getId())
            .phoneNo(entity.getPhoneNo())
            .countryCode(entity.getCountryCode())
            .type(entity.getType())
            .purpose(entity.getPurpose())
            .phoneNumberVerified(entity.isPhoneNumberVerified())
            .primary(entity.isPrimary())
            .marketingConsent(entity.isMarketingConsent())
            .twoFactorAuthEnabled(entity.isTwoFactorAuthEnabled())
            .communicationPreference(entity.getCommunicationPreference())
            .verifiedAt(entity.getVerifiedAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .marketingConsentAt(entity.getMarketingConsentAt())
            .build();
    }

    public UserPhoneNo toEntity() {
        UserPhoneNo phoneNo = new UserPhoneNo();
        phoneNo.setId(this.id);
        phoneNo.setPhoneNo(this.phoneNo);
        phoneNo.setCountryCode(this.countryCode);
        phoneNo.setType(this.type);
        phoneNo.setPurpose(this.purpose);
        phoneNo.setPhoneNumberVerified(this.phoneNumberVerified);
        phoneNo.setPrimary(this.primary);
        phoneNo.setMarketingConsent(this.marketingConsent);
        phoneNo.setTwoFactorAuthEnabled(this.twoFactorAuthEnabled);
        phoneNo.setCommunicationPreference(this.communicationPreference);
        phoneNo.setVerifiedAt(this.verifiedAt);
        phoneNo.setCreatedAt(this.createdAt);
        phoneNo.setUpdatedAt(this.updatedAt);
        phoneNo.setMarketingConsentAt(this.marketingConsentAt);
        
        if (this.getUser() != null) {
            phoneNo.setUsers(this.getUser().toEntity());
        }

        return phoneNo;
    }

    // Masked Phone Number for Privacy
    public String getMaskedPhoneNumber() {
        if (phoneNo == null || phoneNo.length() < 4) {
            return phoneNo;
        }
        return countryCode + " ****" + phoneNo.substring(phoneNo.length() - 4);
    }
}
