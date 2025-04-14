package com.v01.techgear_server.product.mapping;

import java.util.*;

import com.v01.techgear_server.product.model.ProductDetail;
import com.v01.techgear_server.product.dto.ProductDetailDTO;
import org.mapstruct.*;

import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
		ProductMapper.class})
public interface ProductDetailMapper extends BaseMapper<ProductDetail, ProductDetailDTO> {

	@Override
	@Mapping(target = "productDescription", source = "productDetailsDesc")
	@Mapping(target = "warranty", source = "warranty")
	@Mapping(target = "technicalSpecs", source = "technicalSpecifications")
	@Mapping(target = "product", source = "product.productId", ignore = true)
	ProductDetailDTO toDTO(ProductDetail productDetail);

	@Override
	@Mapping(target = "productDetailsDesc", source = "productDescription")
	@Mapping(target = "specifications", ignore = true)
	@Mapping(target = "voucherCode", ignore = true)
	@Mapping(target = "media", ignore = true)
	@Mapping(target = "warranty", source = "warranty")
	@Mapping(target = "technicalSpecifications", source = "technicalSpecs")
	@Mapping(target = "product", source = "product.id", ignore = true)
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
}
