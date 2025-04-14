package com.v01.techgear_server.shipping.dto;

import java.util.List;

import com.v01.techgear_server.order.dto.OrderDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for Shipper")
public class ShipperDTO {

    @Schema(description = "Shipper ID", example = "1")
    private Long shipperId;

    @Schema(description = "Shipper Name", example = "UPS")
    private String shipperName;

    @Schema(description="Shipper rating", example = "4.5")
    private List<ShipperRatingDTO> shipperRatings;

    @Schema(description = "List of Orders")
    private List<OrderDTO> orders;
}
