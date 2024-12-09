package com.v01.techgear_server.dto;

import com.v01.techgear_server.model.ShippingDetails;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ShippingMethodDTO", description = "Shipping Method")
public class ShippingMethodDTO {

    @Schema(description = "Shipping Method ID", example = "1")
    private Integer shippingMethodId;
    @Schema(description = "Shipping Method Name", example = "UPS")
    private String shippingMethodName;
    @Schema(description = "Shipping Method Description", example = "United Parcel Service")
    private String shippingMethodDescription;
    @Schema(description = "Shipping Method Cost", example = "10.99")
    private Double shippingMethodCost;
    @Schema(description = "Delivery Time", example = "1-3 days")
    private String deliveryTime;
    @Schema(description = "Shipping Details associated with the Shipping Method")
    private List<ShippingDetails> shippingDetails;
}
