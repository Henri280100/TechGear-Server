package com.v01.techgear_server.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {
    @Schema(description = "Public product detail identifier")
    private Long id;

    @Schema(description = "Warranty details")
    private String warranty;

    @Schema(description = "Product description")
    private String productDescription;

    @Schema(description = "Technical specifications")
    private String technicalSpecs;

    @Schema(description = "Product reference")
    private ProductDTO product;

}
