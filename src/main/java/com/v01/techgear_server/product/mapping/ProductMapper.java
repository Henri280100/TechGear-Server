package com.v01.techgear_server.product.mapping;

import com.v01.techgear_server.shared.dto.ImageDTO;
import com.v01.techgear_server.enums.ProductAvailability;
import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.model.ProductDetail;
import com.v01.techgear_server.utils.BaseMapper;
import org.mapstruct.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper extends BaseMapper<Product, ProductDTO> {
    Logger log = LoggerFactory.getLogger(ProductMapper.class);


    @Mapping(target = "productSlug", source = "slug")
    @Mapping(target = "productTags", source = "tags")
    @Mapping(target = "finalPrice", expression = "java(getFinalPriceFromProductDetails(product))")
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
    @Mapping(target = "productDetails", ignore = true)
    @Mapping(target = "category.categoryName", source = "productCategory")
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

    // Custom method to get finalPrice from Product's productDetails
    default BigDecimal getFinalPriceFromProductDetails(Product product) {
        if (product == null || product.getProductDetails() == null || product.getProductDetails().isEmpty()) {
            return BigDecimal.ZERO; // Return default value if no product details
        }

        return product.getProductDetails().stream()
                .map(ProductDetail::getFinalPrice) // Get finalPrice from each ProductDetail
                .filter(Objects::nonNull) // Only include non-null finalPrices
                .min(BigDecimal::compareTo) // Get the minimum finalPrice (or change to another logic)
                .orElse(BigDecimal.ZERO); // Default to zero if no finalPrice found
    }

    @AfterMapping
    default void setFinalPriceInProductDetails(ProductDTO dto, @MappingTarget Product entity) {
        if (dto.getFinalPrice() != null) {
            ProductDetail detail = new ProductDetail();
            detail.setFinalPrice(dto.getFinalPrice());
            detail.setProduct(entity); // maintain bidirectional relationship if needed
            entity.setProductDetails(List.of(detail));
        }
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
        Long productId = null;
        if (productIdObj instanceof Number) {
            productId = ((Number) productIdObj).longValue();
            productDTO.setId(productId);
        }

        productDTO.setProductName((String) map.get("name"));
        productDTO.setProductDescription((String) map.get("productDescription"));

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
        Object finalPriceObj = map.get("finalPrice");

        if (finalPriceObj instanceof Number) {
            // Handle case where the value is a Number (could be Double, Integer, etc.)
            productDTO.setFinalPrice(new BigDecimal(finalPriceObj.toString()));  // Convert Number to BigDecimal
        } else if (finalPriceObj instanceof String finalPriceStr) {
            try {
                productDTO.setFinalPrice(new BigDecimal(finalPriceStr));
            } catch (NumberFormatException e) {
                log.warn("Invalid finalPrice format: {}", finalPriceStr);

                productDTO.setFinalPrice(BigDecimal.ZERO);
            }
        } else {
            productDTO.setFinalPrice(BigDecimal.ZERO);
        }

        Object tagsObj = map.get("tags");

        if (tagsObj instanceof List<?>) {
            List<?> rawList = (List<?>) tagsObj;

            // Convert each element to string (if it's not already)
            List<String> tagList = rawList.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString) // Ensures each item is converted to a String
                    .collect(Collectors.toList());

            productDTO.setProductTags(tagList);
        } else {
            productDTO.setProductTags(new ArrayList<>());
        }




        return productDTO;
    }

}
