package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.CommunicationPreference;
import com.v01.techgear_server.enums.PhoneNumberPurpose;
import com.v01.techgear_server.enums.PhoneNumberType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserPhoneNoDTO", description = "Phone number details of user")
public class UserPhoneNoDTO {
    
    @Schema(description = "Phone number ID", example = "1")
    private Long id;

    @Schema(description = "Phone number", example = "+1 1234567890")
    private String phoneNo;

    @Schema(description = "Country code", example = "+1")
    private String countryCode;

    @Schema(description = "Phone number type", example = "PERSONAL")
    private PhoneNumberType type;

    @Schema(description = "Phone number purpose", example = "ORDER_UPDATES")
    private PhoneNumberPurpose purpose;

    @Schema(description = "Is phone number verified", example = "true")
    private boolean phoneNumberVerified;

    @Schema(description = "Is phone number primary", example = "true")
    private boolean primary;

    @Schema(description = "Communication preference", example = "SMS")
    private CommunicationPreference communicationPreference;

    @Schema(description = "Account details associated with phone number")
    private AccountDetailsDTO accountDetailsDTO;
}
