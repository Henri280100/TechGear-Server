package com.v01.techgear_server.dto;

import java.util.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailDTO {
    @Schema(description = "Product detail ID", example = "1")
    @Positive(message = "Product detail ID must be a positive number")
    private Long id;
    
    @Schema(description = "Warranty")
    private String warranty;
    
    @Schema(description = "Voucher code")
    private String voucherCode;
    
    @Schema(description = "Technical specifications")
    private String technicalSpecifications;
    
    @Schema(description = "Description")
    private String description;
    
    @Schema(description = "Product")
    private ProductDTO product;
    
    @Schema(description = "Invoice details")
    private List<InvoiceDetailsDTO> invoiceDetails;
    
    @Schema(description = "Product specifications")
    private List<ProductSpecificationDTO> specifications;
}
