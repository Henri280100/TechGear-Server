package com.v01.techgear_server.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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

    @Schema(description = "List of Orders")
    private List<OrderDTO> orders;
}
