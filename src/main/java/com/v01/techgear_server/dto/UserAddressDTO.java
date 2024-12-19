package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.AddressTypes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Data Transfer Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserAddressDTO {

    @Schema(description = "Unique identifier of the address", example = "1")
    private Long addressId;

    @Schema(description = "First line of the address", example = "123 Main St")
    private String addressLineOne;

    @Schema(description = "Second line of the address", example = "Apt 4B")
    private String addressLineTwo;

    @Schema(description = "City of the address", example = "New York")
    private String city;

    @Schema(description = "State or province of the address", example = "NY")
    private String stateProvince;

    @Schema(description = "ZIP or postal code of the address", example = "10001")
    private String zipPostalCode;

    @Schema(description = "Country of the address", example = "USA")
    private String country;

    @Schema(description = "Type of the address", example = "HOME")
    private AddressTypes type;

    @Schema(description = "Indicates if this is the primary address", example = "true")
    private boolean primaryAddress;

    @Schema(description = "Account details associated with the address")
    private AccountDetailsDTO accountDetails;

    @Schema(description = "Timestamp when the address was created", example = "2023-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the address was last updated", example = "2023-01-02T12:00:00")
    private LocalDateTime updatedAt;
}
