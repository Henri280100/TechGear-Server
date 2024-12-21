package com.v01.techgear_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecificationDTO {
    @Schema(description = "Specification ID")
    private Long specId;

    @Schema(description = "Name of the specification")
    private String specsName;

    @Schema(description = "Value of the specification")
    private String specValue;

    @Schema(description = "Image associated with the specification")
    private ImageDTO specImage;

    @Schema(description = "Icon representing the specification")
    private String icon;

    @Schema(description = "Details of the product associated with the specification")
    private ProductDetailDTO productDetails;
}
