package com.v01.techgear_server.product.dto;

import com.v01.techgear_server.common.dto.ImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecificationDTO {
    @Schema(description = "Specification ID")
    private Long productSpecId;

    @Schema(description = "Name of the specification")
    private String productSpecName;

    @Schema(description = "Value of the specification")
    private String productSpecValue;

    @Schema(description = "Image associated with the specification")
    private ImageDTO productSpecImage;

    @Schema(description = "Icon representing the specification")
    private String productSpecIcon;

    @Schema(description = "Details of the product associated with the specification")
    private ProductDetailDTO detailDTO;
}
