package com.v01.techgear_server.mapping.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.OrderItemsDTO;
import com.v01.techgear_server.dto.ProductDTO;
import com.v01.techgear_server.dto.ProductDetailDTO;
import com.v01.techgear_server.dto.ProductRatingDTO;
import com.v01.techgear_server.dto.WishlistItemsDTO;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.OrderItems;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.model.ProductDetail;
import com.v01.techgear_server.model.ProductRating;
import com.v01.techgear_server.model.WishlistItems;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        ImageMapper.class,
        ProductDetailMapper.class,
        ProductRatingMapper.class,
        OrderItemMapper.class,
        WishlistMapper.class
})
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
    /**
     * Maps a {@link Product} to a {@link ProductDTO}.
     *
     * @param entity the entity to map
     * @return the mapped DTO
     */
    @Mapping(target = "productId", source = "id")
    @Mapping(target = "productDescription", source = "description")
    @Mapping(target = "image", qualifiedByName = "mapImage")
    @Mapping(target = "productDetail", qualifiedByName = "mapProductDetail")
    @Mapping(target = "productRatings", qualifiedByName = "mapProductRatings")
    @Mapping(target = "orderItems", qualifiedByName = "mapOrderItems")
    @Mapping(target = "wishlistItems", qualifiedByName = "mapWishlistItems")
    ProductDTO toDto(Product entity);

    /**
     * Maps a {@link ProductDTO} to a {@link Product}.
     *
     * @param dto the DTO to map
     * @return the mapped entity
     */
    @Mapping(target = "id", source = "productId")
    @Mapping(target = "description", source = "productDescription")
    @Mapping(target = "image", qualifiedByName = "mapImageEntity")
    @Mapping(target = "productDetail", qualifiedByName = "mapProductDetailEntity")
    @Mapping(target = "productRatings", qualifiedByName = "mapProductRatingsEntity")
    @Mapping(target = "orderItems", qualifiedByName = "mapOrderItemsEntity")
    @Mapping(target = "wishlistItems", qualifiedByName = "mapWishlistItemsEntity")
    Product toEntity(ProductDTO dto);

    /**
     * Maps a list of {@link Product} to a list of {@link ProductDTO}.
     *
     * @param entities the list of entities to map
     * @return the mapped list of DTOs
     */
    List<ProductDTO> toDtoList(List<Product> entities);

    /**
     * Maps a list of {@link ProductDTO} to a list of {@link Product}.
     *
     * @param dtos the list of DTOs to map
     * @return the mapped list of entities
     */
    List<Product> toEntityList(List<ProductDTO> dtos);

    // Custom mapping methods for nested objects
    @Named("mapImage")
    default ImageDTO mapImageToDto(Optional<Image> imageOpt) {
        return imageOpt.map(image -> ImageDTO.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .build())
                .orElse(null);
    }

    @Named("mapImageEntity")
    default Image mapImageToEntity(Optional<ImageDTO> imageDTOOpt) {
        return imageDTOOpt.map(imageDTO -> {
            Image image = new Image();
            image.setId(imageDTO.getId());
            image.setImageUrl(imageDTO.getImageUrl());
            return image;
        }).orElse(null);
    }

    @Named("mapProductDetail")
    default ProductDetailDTO mapProductDetailToDto(Optional<ProductDetail> productDetailOpt) {
        return productDetailOpt.map(productDetail -> ProductDetailDTO.builder()
                .id(productDetail.getId())
                .technicalSpecifications(productDetail.getTechnicalSpecifications())
                .build())
                .orElse(null);
    }

    @Named("mapProductDetailEntity")
    default ProductDetail mapProductDetailToEntity(Optional<ProductDetailDTO> productDetailDTOOpt) {
        return productDetailDTOOpt.map(productDetailDTO -> {
            ProductDetail productDetail = new ProductDetail();
            productDetail.setId(productDetailDTO.getId());
            productDetail.setTechnicalSpecifications(productDetailDTO.getTechnicalSpecifications());
            return productDetail;
        }).orElse(null);
    }

    @Named("mapProductRatings")
    default List<ProductRatingDTO> mapProductRatingsToDto(List<ProductRating> ratings) {

        return ratings == null ? Collections.emptyList()
                : ratings.stream()
                        .map(rating -> ProductRatingDTO.builder()
                                .productRatingId(rating.getProductRatingId())
                                .rating(rating.getRating())
                                .build())
                        .toList();
    }

    @Named("mapProductRatingsEntity")
    default List<ProductRating> mapProductRatingsToEntity(List<ProductRatingDTO> ratingDTOs) {

        return ratingDTOs == null ? Collections.emptyList()
                : ratingDTOs.stream()
                        .map(ratingDTO -> {
                            ProductRating rating = new ProductRating();
                            rating.setProductRatingId(ratingDTO.getProductRatingId());
                            rating.setRating(ratingDTO.getRating());
                            return rating;
                        })
                        .toList();
    }

    @Named("mapOrderItems")
    default List<OrderItemsDTO> mapOrderItemsToDto(List<OrderItems> orderItems) {

        return orderItems == null ? Collections.emptyList()
                : orderItems.stream()
                        .map(orderItem -> OrderItemsDTO.builder()
                                .orderItemId(orderItem.getOrderItemsId())
                                .quantity(orderItem.getQuantity())
                                .build())
                        .toList();
    }

    @Named("mapOrderItemsEntity")
    default List<OrderItems> mapOrderItemsToEntity(List<OrderItemsDTO> orderItemDTOs) {

        return orderItemDTOs == null ? Collections.emptyList()
                : orderItemDTOs.stream()
                        .map(orderItemDTO -> {
                            OrderItems orderItem = new OrderItems();
                            orderItem.setOrderItemsId(orderItemDTO.getOrderItemId());
                            orderItem.setQuantity(orderItemDTO.getQuantity());
                            return orderItem;
                        })
                        .toList();
    }

    @Named("mapWishlistItems")
    default List<WishlistItemsDTO> mapWishlistItemsToDto(List<WishlistItems> wishlistItems) {

        return wishlistItems == null ? Collections.emptyList()
                : wishlistItems.stream()
                        .map(wishlistItem -> WishlistItemsDTO.builder()
                                .id(wishlistItem.getId())
                                .productName(wishlistItem.getProductName())
                                .build())
                        .toList();
    }

    @Named("mapWishlistItemsEntity")
    default List<WishlistItems> mapWishlistItemsToEntity(List<WishlistItemsDTO> wishlistItemDTOs) {

        return wishlistItemDTOs == null ? Collections.emptyList()
                : wishlistItemDTOs.stream()
                        .map(wishlistItemDTO -> {
                            WishlistItems wishlistItem = new WishlistItems();
                            wishlistItem.setId(wishlistItemDTO.getId());
                            wishlistItem.setProductName(wishlistItemDTO.getProductName());
                            return wishlistItem;
                        })
                        .toList();
    }
}
