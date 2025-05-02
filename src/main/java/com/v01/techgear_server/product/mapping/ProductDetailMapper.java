package com.v01.techgear_server.product.mapping;

import com.v01.techgear_server.product.dto.ProductDetailDTO;
import com.v01.techgear_server.product.dto.ProductSpecificationDTO;
import com.v01.techgear_server.product.model.ProductDetail;
import com.v01.techgear_server.product.model.ProductSpecification;
import com.v01.techgear_server.utils.BaseMapper;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        ProductMapper.class})
public interface ProductDetailMapper extends BaseMapper<ProductDetail, ProductDetailDTO> {

    @Override
    @Mapping(target = "specification", source = "specifications")
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productDayLeft", source = "dayLeft")
    @Mapping(target = "video", source = "detailVideoUrl")
    @Mapping(target = "image", source = "detailImageUrl")
    @Mapping(target = "detailsId", source = "id")
    @Mapping(target = "productDescription", source = "productDetailsDesc")
    @Mapping(target = "warranty", source = "warranty")
    ProductDetailDTO toDTO(ProductDetail productDetail);

    @Override
    @Mapping(target = "specifications", source = "specification")
    @Mapping(target = "product.productId", source = "productId")
    @Mapping(target = "dayLeft", source = "productDayLeft")
    @Mapping(target = "id", source = "detailsId")
    @Mapping(target = "detailVideoUrl", source = "video")
    @Mapping(target = "detailImageUrl", source = "image")
    @Mapping(target = "productDetailsDesc", source = "productDescription")
    @Mapping(target = "voucherCode")
    @Mapping(target = "warranty", source = "warranty")
    ProductDetail toEntity(ProductDetailDTO productDetailDTO);

    @Override
    default List<ProductDetailDTO> toDTOList(List<ProductDetail> productDetails) {
        return productDetails == null ? Collections.emptyList() : productDetails.stream().map(this::toDTO).toList();
    }

    @Override
    default List<ProductDetail> toEntityList(List<ProductDetailDTO> productDetailDTOs) {
        return productDetailDTOs == null ? Collections.emptyList()
                : productDetailDTOs.stream().map(this::toEntity).toList();
    }

    @Mapping(target = "productSpecId", source = "specId")
    @Mapping(target = "productSpecName", source = "specName")
    @Mapping(target = "productSpecValue", source = "specValue")
    @Mapping(target = "productSpecGuarantee", source = "guarantee")
    ProductSpecificationDTO toSpecDTO(ProductSpecification productSpecification);

    @AfterMapping
    default void filterInvalidSpecifications(@MappingTarget ProductDetail productDetail) {
        if (productDetail.getSpecifications() != null) {
            productDetail.setSpecifications(
                    productDetail.getSpecifications().stream()
                            .filter(spec -> spec.getSpecName() != null && !spec.getSpecName().trim().isEmpty()
                                    && spec.getSpecValue() != null && !spec.getSpecValue().trim().isEmpty()
                                    && spec.getGuarantee() != null && !spec.getGuarantee().trim().isEmpty())
                            .collect(Collectors.toList())
            );
        }
    }

}
