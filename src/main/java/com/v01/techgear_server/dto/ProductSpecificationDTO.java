package com.v01.techgear_server.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecificationDTO {
    private Long specId;
    private String specsName;
    private String specValue;
    private ImageDTO specImage;
    private String icon;
    private ProductDetailDTO productDetails;
}
