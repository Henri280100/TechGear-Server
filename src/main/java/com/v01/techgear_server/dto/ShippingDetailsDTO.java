package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ShippingDetailsDTO", description = "DTO for shipping details")
public class ShippingDetailsDTO {
    @Schema(description = "Shipping details ID", example = "123")
    private Long shippingDetailsId;

    @Schema(description = "Username", example = "John Doe")
    private String shipperName;

    @Schema(description = "Shipping address line 1", example = "123 Main Street")
    private String addressLineOne;

    @Schema(description = "Shipping address line 2", example = "Apt 4B")
    private String addressLineTwo;

    @Schema(description = "Shipping city", example = "New York")
    private String city;

    @Schema(description = "Shipping state/province", example = "NY")
    private String state;

    @Schema(description = "Shipping country", example = "USA")
    private String country;

    @Schema(description = "Shipping method associated with Shipping details")
    private ShippingMethodDTO shippingMethodDTO;

}
