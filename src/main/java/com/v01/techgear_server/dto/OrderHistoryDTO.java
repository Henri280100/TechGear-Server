package com.v01.techgear_server.dto;

import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description="Order history data transfer object")
public class OrderHistoryDTO {

    @Schema(description = "Order history ID", example = "123")
    @Positive(message = "Order history ID must be a positive number")
    private Long orderHistoryId;

    @Schema(description="User associated with the order history")
    private UserDTO user;

    @Schema(description="Order items associated with the order history")
    private List<OrderItemsDTO> orderItems;


}