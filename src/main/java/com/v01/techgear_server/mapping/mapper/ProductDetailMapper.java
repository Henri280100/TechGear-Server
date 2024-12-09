package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;

import com.v01.techgear_server.dto.*;

import com.v01.techgear_server.model.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        ProductMapper.class,
        InvoiceDetailsMapper.class,
        ProductSpecificationMapper.class
})
public interface ProductDetailMapper {

    @Mapping(target = "id", source = "productDetailId")
    @Mapping(target = "warranty", source = "warranty")
    @Mapping(target = "technicalSpecifications", source = "technicalSpecifications")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "product", qualifiedByName = "mapProduct")
    @Mapping(target = "invoiceDetails", qualifiedByName = "mapInvoiceDetails")
    @Mapping(target = "productSpecification", qualifiedByName = "mapProductSpecification")
    ProductDetailDTO toDto(ProductDetail entity);

    @Mapping(source = "productDetailId", target = "id")
    @Mapping(source = "warranty", target = "warranty")
    @Mapping(source = "technicalSpecifications", target = "technicalSpecifications")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "product", qualifiedByName = "mapProductEntity")
    @Mapping(target = "invoiceDetails", qualifiedByName = "mapInvoiceDetailsEntity")
    @Mapping(target = "productSpecification", qualifiedByName = "mapProductSpecificationEntity")
    ProductDetail toEntity(ProductDetailDTO dto);

    /**
     * Maps a list of {@link ProductDetail} to a list of {@link ProductDetailDTO}.
     *
     * @param entities the list of entities to map
     * @return the mapped list of DTOs
     */
    List<ProductDetailDTO> toDtoList(List<ProductDetail> entities);

    /**
     * Maps a list of {@link ProductDetailDTO} to a list of {@link ProductDetail}.
     *
     * @param dtos the list of DTOs to map
     * @return the mapped list of entities
     */
    List<ProductDetail> toEntityList(List<ProductDetailDTO> dtos);

    // Custom mapping methods for nested objects
    @Named("mapProduct")
    default ProductDTO mapProductToDto(Optional<Product> productOpt) {
        return productOpt.map(product -> ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .productDescription(product.getProductDescription())
                .build())
                .orElse(null);

    }

    @Named("mapProductEntity")
    default Product mapProductToEntity(Optional<ProductDTO> productDTOOpt) {
        return productDTOOpt.map(productDTO -> {
            Product product = new Product();
            product.setProductId(productDTO.getProductId());
            product.setName(productDTO.getName());
            product.setPrice(productDTO.getPrice());
            product.setProductDescription(productDTO.getProductDescription());
            return product;
        }).orElse(null);
    }

    @Named("mapInvoiceDetailsList")
    default List<InvoiceDetailsDTO> mapInvoiceDetailsToDtoList(List<InvoiceDetails> invoiceDetailsList) {
        return invoiceDetailsList == null ? Collections.emptyList()
                : invoiceDetailsList.stream()
                        .map(invoiceDetails -> InvoiceDetailsDTO.builder()
                                .invoiceDetailsId(invoiceDetails.getInvoiceDetailsId())
                                .lineTotal(invoiceDetails.getLineTotal())
                                .build())
                        .toList();
    }

    @Named("mapInvoiceDetailsListEntity")
    default List<InvoiceDetails> mapInvoiceDetailsToEntityList(List<InvoiceDetailsDTO> invoiceDetailsDTOList) {
        return invoiceDetailsDTOList == null ? Collections.emptyList()
                : invoiceDetailsDTOList.stream()
                        .map(invoiceDetailsDTO -> {
                            InvoiceDetails invoiceDetails = new InvoiceDetails();
                            invoiceDetails.setInvoiceDetailsId(invoiceDetailsDTO.getInvoiceDetailsId());
                            invoiceDetails.setLineTotal(invoiceDetailsDTO.getLineTotal());
                            return invoiceDetails;
                        })
                        .toList();
    }

    @Named("mapProductSpecificationList")
    default List<ProductSpecificationDTO> mapProductSpecificationToDtoList(
            List<ProductSpecification> productSpecificationList) {
        return productSpecificationList == null ? Collections.emptyList()
                : productSpecificationList.stream()
                        .map(productSpecification -> ProductSpecificationDTO.builder()
                                .specId(productSpecification.getSpecId())
                                .specsName(productSpecification.getSpecsName())
                                .specValue(productSpecification.getSpecValue())
                                .build())
                        .toList();
    }

    @Named("mapProductSpecificationListEntity")
    default List<ProductSpecification> mapProductSpecificationToEntityList(
            List<ProductSpecificationDTO> productSpecificationDTOList) {
        return productSpecificationDTOList == null ? Collections.emptyList()
                : productSpecificationDTOList.stream()
                        .map(productSpecificationDTO -> {
                            ProductSpecification productSpecification = new ProductSpecification();
                            productSpecification.setSpecId(productSpecificationDTO.getSpecId());
                            productSpecification.setSpecsName(productSpecificationDTO.getSpecsName());
                            productSpecification.setSpecValue(productSpecificationDTO.getSpecValue());
                            return productSpecification;
                        })
                        .toList();
    }
}
