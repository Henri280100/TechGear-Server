package com.v01.techgear_server.dto;

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
    private Long id;

    @Schema(description = "First line of the address", example = "123 Main St")
    private String userAddressLineOne;

    @Schema(description = "Second line of the address", example = "Apt 4B")
    private String userAddressLineTwo;

    @Schema(description = "City of the address", example = "New York")
    private String cityAddress;

    @Schema(description = "State or province of the address", example = "NY")
    private String state;

    @Schema(description = "ZIP or postal code of the address", example = "10001")
    private String zipPostalCode;

    @Schema(description = "Type of the address", example = "HOME")
    private AddressTypes userAddressType;

    @Schema(description = "Country of the address", example = "USA")
    private String userCountry;

    @Schema(description = "Account details")
    private AccountDetailsDTO accountDetailsDTO;
}
