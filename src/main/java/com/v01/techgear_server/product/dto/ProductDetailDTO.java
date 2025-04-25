package com.v01.techgear_server.product.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.v01.techgear_server.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {
    @Schema(description = "Public product detail identifier")
    private Long detailsId;

    @Schema(description = "Product Id")
    private Long productId;

    @Schema(description = "Warranty details")
    private String warranty;

    @Schema(description = "Voucher code")
    private String voucherCode;

    @Schema(description = "Product price")
    private BigDecimal price;

    @Schema(description = "Final price after discounts")
    private BigDecimal finalPrice;

    @Schema(description = "Product description")
    private String productDescription;

    @Schema(description = "Technical specifications")
    private String technicalSpecs;

    @Schema(description = "Product colors")
    private String colors;

    @Schema(description = "Product hype")
    private String hype;

    @Schema(description = "Product title")
    private String title;

    @Schema(description = "Product release date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Schema(description = "Product release day left")
    private String productDayLeft;

    @Schema(description = "Product detail image")
    private String image;

    @Schema(description = "Product detail video")
    private String video;

    @Schema(description = "Product status (New Arrival, Popular, etc.)")
    private ProductStatus productStatus;


}
