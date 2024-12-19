package com.v01.techgear_server.mapping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.v01.techgear_server.dto.ProductDTO;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        ImageMapper.class,
        ProductDetailMapper.class,
        ProductRatingMapper.class,
        OrderItemsMapper.class,
        WishlistMapper.class
}, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ProductMapper extends BaseMapper<Product, ProductDTO>{
    @Override
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "productDescription", source = "productDescription")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "image", source = "image")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "sku", source = "sku", 
             qualifiedByName = "validateSku")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "productDetail", source = "productDetail")
    @Mapping(target = "productRatings", source = "productRatings", 
             qualifiedByName = "mapProductRatings")
    @Mapping(target = "orderItems", source = "orderItems", 
             qualifiedByName = "mapOrderItems")
    @Mapping(target = "wishlistItems", source = "wishlistItems", 
             qualifiedByName = "mapWishlistItems")
    ProductDTO toDTO(Product product);

    @Override
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "productDescription", source = "productDescription")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "image", source = "image")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "sku", source = "sku", 
             qualifiedByName = "validateSku")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "productDetail", source = "productDetail")
    @Mapping(target = "productRatings", source = "productRatings", 
             qualifiedByName = "mapProductRatings")
    @Mapping(target = "orderItems", source = "orderItems", 
             qualifiedByName = "mapOrderItems")
    @Mapping(target = "wishlistItems", source = "wishlistItems", 
             qualifiedByName = "mapWishlistItems")
    Product toEntity(ProductDTO productDTO);

    

}
