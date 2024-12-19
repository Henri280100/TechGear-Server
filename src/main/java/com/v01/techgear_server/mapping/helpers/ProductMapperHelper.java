package com.v01.techgear_server.mapping.helpers;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Named;

import com.v01.techgear_server.dto.OrderItemsDTO;
import com.v01.techgear_server.dto.ProductRatingDTO;
import com.v01.techgear_server.dto.WishlistItemsDTO;
import com.v01.techgear_server.model.OrderItems;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.model.ProductRating;
import com.v01.techgear_server.model.WishlistItems;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductMapperHelper {

    private ProductMapperHelper() {
    }

    @Named("mapProductRatings")
    public static List<ProductRatingDTO> mapProductRatings(List<ProductRating> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return Collections.emptyList();
        }
        return ratings.stream()
                .map(ProductMapperHelper::mapProductRating)
                .toList();
    }

    private static ProductRatingDTO mapProductRating(ProductRating rating) {
        ProductRatingDTO dto = new ProductRatingDTO();
        dto.setProductRatingId(rating.getProductRatingId());
        dto.setRating(rating.getRating());
        dto.setRatingDate(rating.getRatingDate());
        return dto;
    }

    @Named("mapOrderItems")
    public static List<OrderItemsDTO> mapOrderItems(List<OrderItems> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(ProductMapperHelper::mapOrderItem)
                .toList();

    }

    private static OrderItemsDTO mapOrderItem(OrderItems item) {

        OrderItemsDTO dto = new OrderItemsDTO();
        dto.setOrderItemId(item.getOrderItemsId());
        dto.setQuantity(item.getQuantity());
        return dto;

    }

    @Named("mapWishlistItems")
    public static List<WishlistItemsDTO> mapWishlistItems(List<WishlistItems> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .map(ProductMapperHelper::mapWishlistItem)
                .toList();
    }

    private static WishlistItemsDTO mapWishlistItem(WishlistItems item) {
        WishlistItemsDTO dto = new WishlistItemsDTO();
        dto.setId(item.getId());
        return dto;
    }

    // Price-related utilities
    public static double calculateDiscountedPrice(Product product, double discountPercentage) {
        return product.getPrice() * (1 - (discountPercentage / 100));
    }

}
