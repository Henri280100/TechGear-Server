package com.v01.techgear_server.product.mapping;

import com.v01.techgear_server.common.dto.ImageDTO;
import com.v01.techgear_server.enums.ProductAvailability;
import com.v01.techgear_server.enums.ProductStatus;
import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.dto.ProductFilterSortResponse;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductCategory;
import com.v01.techgear_server.product.model.ProductDetail;
import com.v01.techgear_server.utils.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper extends BaseMapper<Product, ProductDTO> {
    Logger log = LoggerFactory.getLogger(ProductMapper.class);

    @Override
    @Mapping(target = "productTags", source = "tags")
    @Mapping(target = "productDetailPrice", expression = "java(getDefaultPrice(product.getProductDetails()))")
    @Mapping(target = "productStockLevel", source = "stockLevel")
    @Mapping(target = "productName", source = "name")
    @Mapping(target = "productMinPrice", source = "minPrice")
    @Mapping(target = "productMaxPrice", source = "maxPrice")
    @Mapping(target = "productImage", source = "imageUrl")
    @Mapping(target = "productCategory", source = "category.categoryName")
    @Mapping(target = "productBrand", source = "brand")
    @Mapping(target = "productAvailability", source = "availability")
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "productFeatures", source = "features")
    ProductDTO toDTO(Product product);

    @Override
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "category.categoryName", source = "productCategory")
    @Mapping(target = "productDetails", source = "productDetails")
    @Mapping(target = "tags", source = "productTags")
    @Mapping(target = "wishlistItems", ignore = true)
    @Mapping(target = "stockLevel", source = "productStockLevel")
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "productRatings", ignore = true)
    @Mapping(target = "productId", source = "id")
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "name", source = "productName")
    @Mapping(target = "minPrice", source = "productMinPrice")
    @Mapping(target = "maxPrice", source = "productMaxPrice")
    @Mapping(target = "imageUrl", source = "productImage")
    @Mapping(target = "brand", source = "productBrand")
    @Mapping(target = "availability", source = "productAvailability")
    @Mapping(target = "features", source = "productFeatures")
    @Mapping(target = "discounts", ignore = true)
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

//
//    default String getVoucherCode(List<ProductDetail> details) {
//        return details.stream()
//                .map(ProductDetail::getVoucherCode)
//                .filter(voucherCode -> voucherCode != null && !voucherCode.isEmpty())
//                .findFirst()
//                .orElse(null);
//    }

    default BigDecimal getDefaultPrice(List<ProductDetail> productDetails) {
        if (productDetails == null || productDetails.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return productDetails.stream()
                .filter(detail -> detail.getProductStatus() == ProductStatus.NEW)
                .map(ProductDetail::getFinalPrice)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    @Mapping(target = "productPrice", expression = "java(getDefaultPrice(product.getProductDetails()))")
    @Mapping(target = "id", ignore = true)
    ProductFilterSortResponse productFilterSortToDTO(Product product);

    default String mapCategory(ProductCategory category) {
        if (category == null) return null;
        return category.getCategoryName();
    }

    // String -> ImageDTO
    default ImageDTO map(String url) {
        if (url == null || url.isEmpty()) return null;
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageUrl(url);
        return imageDTO;
    }

    // ImageDTO -> String
    default String map(ImageDTO imageDTO) {
        if (imageDTO == null) return null;
        return imageDTO.getImageUrl();
    }

    default ProductDTO toSearchDTO(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        log.info("Mapping document: {}", map);

        ProductDTO productDTO = new ProductDTO();

        Object productIdObj = map.get("productId");
        if (productIdObj instanceof Number) {
            productDTO.setId(((Number) productIdObj).longValue());
        }

        productDTO.setProductName((String) map.get("name"));
        productDTO.setProductDescription((String) map.get("productDescription"));

        Object priceObj = map.get("price");
        if (priceObj instanceof Number) {
            productDTO.setProductDetailPrice(BigDecimal.valueOf(((Number) priceObj).doubleValue()));
        }

        Object availabilityObj = map.get("availability");
        if (availabilityObj instanceof String availabilityStr) {
            try {
                productDTO.setProductAvailability(ProductAvailability.fromValue(availabilityStr));
            } catch (IllegalArgumentException e) {
                log.warn("Unknown productAvailability: {}", availabilityStr);
                productDTO.setProductAvailability(ProductAvailability.UNAVAILABLE);
            }
        }

        productDTO.setProductCategory((String) map.get("category"));
        productDTO.setProductImage((String) map.get("image"));
        productDTO.setProductBrand((String) map.get("brand"));
        productDTO.setProductFeatures((String) map.get("features"));

        return productDTO;
    }

}
