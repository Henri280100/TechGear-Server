package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.AddressTypes;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User Data Transfer Object")
public class UserAddressDTO {
    @Schema(description = "User Address ID", example = "123")
    private Long addressId;

    @Schema(description = "Address Line 1", example = "123 Main Street")
    private String addressLineOne;

    @Schema(description = "Address Line 2", example = "Apt 4B")
    private String addressLineTwo;

    @Schema(description = "City", example = "New York")
    private String city;

    @Schema(description = "State/Province", example = "NY")
    private String stateProvince;

    @Schema(description = "Zip/Postal Code", example = "10001")
    private String zipPostalCode;

    @Schema(description = "Country", example = "USA")
    private String country;

    @Schema(description = "Address Type", example = "HOME")
    private AddressTypes type;

    @Schema(description = "Is Address Primary", example = "true")
    private boolean primaryAddress;

    @Schema(description = "Account details associated with the address")
    private AccountDetailsDTO accountDetails;

    @Schema(description = "Creation Timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last Update Timestamp")
    private LocalDateTime updatedAt;

}
