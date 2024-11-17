package com.v01.techgear_server.dto;

import com.v01.techgear_server.enums.AddressTypes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Billing Address Data Transfer Object")
public class BillingAddressDTO {
    @Schema(description = "Billing Address ID", example = "123")
    @Positive(message = "Billing Address ID must be a positive number")
    private Integer addressId;

    @Schema(description = "Address Line 1", example = "123 Main Street")
    private String addressLine1;

    @Schema(description = "Address Line 2", example = "Apt 4B")
    private String addressLine2;

    @Schema(description="City")
    private String city;
    
    @Schema(description = "State/Province", example = "NY")
    private String stateProvince;

    @Schema(description="Zip/Postal Code", example = "10001")
    private String zipPostalCode;

    @Schema(description = "Country", example = "USA")
    private String country;

    @Schema(description="Address types")
    private AddressTypes addressTypes;

    @Schema(description="Billing Information associated with the address", example = "true")
    private BillingInformationDTO billingInformation;
}