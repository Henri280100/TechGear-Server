package com.v01.techgear_server.product.mapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.v01.techgear_server.common.mapping.ImageMapper;
import com.v01.techgear_server.product.dto.ProductDTO;
import org.mapstruct.*;

import com.v01.techgear_server.product.dto.ProductFilterSortResponse;
import com.v01.techgear_server.enums.ProductAvailability;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
		ImageMapper.class})
public interface ProductMapper extends BaseMapper<Product, ProductDTO> {

	@Override
	@Mapping(target = "productStockLevel", source = "stockLevel")
	@Mapping(target = "productPrice", source = "price")
	@Mapping(target = "productName", source = "name")
	@Mapping(target = "productMinPrice", source = "minPrice")
	@Mapping(target = "productMaxPrice", source = "maxPrice")
	@Mapping(target = "productImage", source = "image")
	@Mapping(target = "productCategory", source = "category")
	@Mapping(target = "productBrand", source = "brand")
	@Mapping(target = "productAvailability", source = "availability")
	@Mapping(target = "id", source = "productId", ignore = true)
	ProductDTO toDTO(Product product);

	@Override
	@Mapping(target = "wishlistItems", ignore = true)
	@Mapping(target = "stockLevel", source = "productStockLevel", ignore = true)
	@Mapping(target = "slug", ignore = true)
	@Mapping(target = "productRatings", ignore = true)
	@Mapping(target = "productId", source = "id", ignore = true)
	@Mapping(target = "productDetail", ignore = true)
	@Mapping(target = "price", source = "productPrice")
	@Mapping(target = "orderItems", ignore = true)
	@Mapping(target = "name", source = "productName")
	@Mapping(target = "minPrice", source = "productMinPrice")
	@Mapping(target = "maxPrice", source = "productMaxPrice")
	@Mapping(target = "image", source = "productImage")
	@Mapping(target = "category", source = "productCategory")
	@Mapping(target = "brand", source = "productBrand")
	@Mapping(target = "availability", source = "productAvailability")
	Product toEntity(ProductDTO productDTO);

	@Override
	default List<ProductDTO> toDTOList(List<Product> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<Product> toEntityList(List<ProductDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "productPrice", source = "price")
	ProductFilterSortResponse productFilterSortToDTO(Product product);

	default ProductDTO toSearchDTO(Map<String, Object> map) {
		if (map == null) {
			return null;
		}

		ProductDTO productDTO = new ProductDTO();
		productDTO.setId((Long) map.get("productId"));
		productDTO.setProductName((String) map.get("name"));
		productDTO.setProductDescription((String) map.get("description"));
		productDTO.setProductPrice((Double) map.get("productPrice"));
		Boolean availability = (Boolean) map.get("availability");
		productDTO.setProductAvailability(
				availability != null && availability ? ProductAvailability.IN_STOCK : ProductAvailability.OUT_OF_STOCK);
		productDTO.setProductCategory((String) map.get("category"));
		return productDTO;
	}

}
