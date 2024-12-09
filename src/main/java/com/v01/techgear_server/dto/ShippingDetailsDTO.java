package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.AddressTypes;
import com.v01.techgear_server.enums.OrderStatus;
import com.v01.techgear_server.enums.PhoneNumberPurpose;
import com.v01.techgear_server.enums.PhoneNumberType;

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
    @Schema(description = "Username", example = "JohnDoe")
    private String username;
    @Schema(description = "Address line 1", example = "123 Main Street")
    private String addressLine1;
    @Schema(description = "Address line 2", example = "Apt 4B")
    private String addressLine2;
    @Schema(description = "City", example = "New York")
    private String city;
    @Schema(description = "State/Province", example = "NY")
    private String state;
    @Schema(description = "Zip/Postal Code", example = "10001")
    private String postalCode;
    @Schema(description = "Country", example = "USA")
    private String country;
    @Schema(description = "Phone number", example = "+1 1234567890")
    private String phoneNumber;
    @Schema(description = "Email", example = "john.doe@example.com")
    private String email;
    @Schema(description = "Shipping date", example = "2021-01-01T00:00:00")
    private LocalDateTime shippingDate;
    @Schema(description = "Estimated delivery date", example = "2021-01-01T00:00:00")
    private LocalDateTime estimatedDeliveryDate;
    @Schema(description = "Shipping method")
    private ShippingMethodDTO shippingMethod;
    @Schema(description = "Order associated with the shipping details")
    private OrderDTO order;
}
