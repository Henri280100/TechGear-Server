package com.v01.techgear_server.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
