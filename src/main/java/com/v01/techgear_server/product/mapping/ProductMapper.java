package com.v01.techgear_server.product.mapping;

import com.v01.techgear_server.common.mapping.ImageMapper;
import com.v01.techgear_server.common.model.Image;
import com.v01.techgear_server.enums.ProductAvailability;
import com.v01.techgear_server.product.dto.ProductDTO;
import com.v01.techgear_server.product.dto.ProductFilterSortResponse;
import com.v01.techgear_server.product.model.Product;
import com.v01.techgear_server.product.search.impl.ProductSchemaServiceImpl;
import com.v01.techgear_server.utils.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        ImageMapper.class})
public interface ProductMapper extends BaseMapper<Product, ProductDTO> {
    Logger log = LoggerFactory.getLogger(ProductMapper.class);

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
    @Mapping(target = "productFeatures", source = "features")
    ProductDTO toDTO(Product product);

    @Override
    @Mapping(target = "wishlistItems", ignore = true)
    @Mapping(target = "stockLevel", source = "productStockLevel")
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productPrice", source = "price")
    ProductFilterSortResponse productFilterSortToDTO(Product product);

    // String -> Image
    default Image map(String url) {
        if (url == null || url.isEmpty()) return null;
        Image image = new Image();
        image.setImageUrl(url);
        return image;
    }

    // Image -> String
    default String map(Image image) {
        if (image == null) return null;
        return image.getImageUrl();
    }

    default ProductDTO toSearchDTO(Map<String, Object> map) {
        if (map == null) {
            return null;
        }

        log.info("Mapping document: {}", map); // Debug log to inspect the document

        ProductDTO productDTO = new ProductDTO();
        // Set productId
        Object productIdObj = map.get("productId");
        if (productIdObj instanceof Number) {
            productDTO.setId(((Number) productIdObj).longValue());
        } else {
            throw new IllegalArgumentException("productId must be a number, found: " + productIdObj);
        }

        // Set name
        productDTO.setProductName((String) map.get("name"));

        // Set productDescription (not description)
        productDTO.setProductDescription((String) map.get("productDescription"));

        // Set price (not productPrice)
        Object priceObj = map.get("price");
        if (priceObj instanceof Number) {
            productDTO.setProductPrice(((Number) priceObj).doubleValue());
        } else {
            log.warn("Price is missing or not a number in document: {}", map);
            productDTO.setProductPrice(0.0); // Default value or handle as needed
        }
        productDTO.setProductAvailability((String) map.get("availability"));

        productDTO.setProductCategory((String) map.get("category"));
        productDTO.setProductImage((String) map.get("productImage"));

        return productDTO;
    }

}
