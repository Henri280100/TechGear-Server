package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;

import com.v01.techgear_server.dto.AccountDetailsDTO;
import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.ProductDTO;
import com.v01.techgear_server.dto.ProductRatingDTO;
import com.v01.techgear_server.dto.ProductSpecificationDTO;
import com.v01.techgear_server.dto.UserPhoneNoDTO;
import com.v01.techgear_server.dto.WishlistDTO;
import com.v01.techgear_server.model.AccountDetails;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.model.ProductRating;
import com.v01.techgear_server.model.ProductSpecification;
import com.v01.techgear_server.model.Wishlist;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        AccountDetailsMapper.class,
        ProductMapper.class,
        ProductRatingMapper.class,
        ProductSpecificationMapper.class,
        WishlistMapper.class,
        WishlistItemsMapper.class
})
public interface ImageMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "accountDetails", qualifiedByName = "accountDetails")
    @Mapping(target = "product", qualifiedByName = "product")
    @Mapping(target = "productRating", qualifiedByName = "productRating")
    @Mapping(target = "productSpecification", qualifiedByName = "productSpecification")
    @Mapping(target = "wishlist", qualifiedByName = "wishlist")
    @Mapping(target = "wishlistItems", qualifiedByName = "wishlistItems")
    ImageDTO toDto(Image image);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "accountDetails", qualifiedByName = "accountDetails")
    @Mapping(target = "product", qualifiedByName = "product")
    @Mapping(target = "productRating", qualifiedByName = "productRating")
    @Mapping(target = "productSpecification", qualifiedByName = "productSpecification")
    @Mapping(target = "wishlist", qualifiedByName = "wishlist")
    @Mapping(target = "wishlistItems", qualifiedByName = "wishlistItems")
    Image toEntity(ImageDTO dto);

    List<ImageDTO> toDtoList(List<Image> entities);

    List<Image> toEntityList(List<ImageDTO> dtos);

    @Named("mapAccountDetails")
    default AccountDetailsDTO mapAccountDetailsToDto(AccountDetails accountDetails) {
        return accountDetails == null ? null
                : AccountDetailsDTO.builder().accountDetailsId(accountDetails.getAccountDetailsId())
                        .firstName(accountDetails.getFirstName())
                        .lastName(accountDetails.getLastName())
                        .totalReview(accountDetails.getTotalReviews()).build();
    }

    @Named("mapAccountDetailsEntity")
    default AccountDetails mapAccountDetailsToEntity(AccountDetailsDTO accountDetailsDTO) {
        if (accountDetailsDTO == null)
            return null;

        AccountDetails accountDetails = new AccountDetails();
        accountDetails.setAccountDetailsId(accountDetailsDTO.getAccountDetailsId());
        accountDetails.setFirstName(accountDetailsDTO.getFirstName());
        accountDetails.setLastName(accountDetailsDTO.getLastName());
        return accountDetails;
    }

    @Named("mapProduct")
    default ProductDTO mapProductToDto(Product product) {
        return product == null ? null
                : ProductDTO.builder().productId(product.getProductId()).name(product.getName())
                        .productDescription(product.getProductDescription()).availability(product.getAvailability())
                        .price(product.getPrice()).build();
    }

    @Named("mapProductEntity")
    default Product mapProductToEntity(ProductDTO productDTO) {
        if (productDTO == null)
            return null;

        Product product = new Product();
        product.setProductId(productDTO.getProductId());
        product.setName(productDTO.getName());
        product.setProductDescription(productDTO.getProductDescription());
        product.setAvailability(productDTO.getAvailability());
        product.setPrice(productDTO.getPrice());
        return product;
    }

    @Named("mapProductRating")
    default ProductRatingDTO mapProductToDto(ProductRating productRating) {
        return productRating == null ? null
                : ProductRatingDTO.builder().productRatingId(productRating.getProductRatingId())
                        .rating(productRating.getRating())
                        .comments(productRating.getComments())
                        .build();
    }

    @Named("mapProductRatingEntity")
    default ProductRating mapProductRatingToEntity(ProductRatingDTO productRatingDTO) {
        if (productRatingDTO == null)
            return null;

        ProductRating productRating = new ProductRating();
        productRating.setProductRatingId(productRatingDTO.getProductRatingId());
        productRating.setRating(productRatingDTO.getRating());
        productRating.setComments(productRatingDTO.getComments());
        return productRating;
    }

    @Named("mapProductSpecification")
    default ProductSpecificationDTO mapProductSpecificationToDto(ProductSpecification productSpecification) {
        return productSpecification == null ? null
                : ProductSpecificationDTO.builder().specId(productSpecification.getSpecId())
                        .specsName(productSpecification.getSpecsName())
                        .specValue(productSpecification.getSpecValue())
                        .build();
    }

    @Named("mapProductSpecificationEntity")
    default ProductSpecification mapProductSpecificationToEntity(ProductSpecificationDTO productSpecificationDTO) {
        if (productSpecificationDTO == null)
            return null;

        ProductSpecification productSpecification = new ProductSpecification();
        productSpecification.setSpecId(productSpecificationDTO.getSpecId());
        productSpecification.setSpecsName(productSpecificationDTO.getSpecsName());
        productSpecification.setSpecValue(productSpecificationDTO.getSpecValue());
        return productSpecification;
    }

    @Named("mapWishlist")
    default WishlistDTO mapWishlistToDto(Wishlist wishlist) {
        return wishlist == null ? null
                : WishlistDTO.builder().wishlistId(wishlist.getWishlistId())
                        .wishlistDescription(wishlist.getWishlistDescription())
                        .totalValue(wishlist.getTotalValue())
                        .priority(wishlist.getPriority())
                        .build();
    }

    @Named("mapWishlistEntity")
    default Wishlist mapWishlistToEntity(WishlistDTO wishlistDTO) {
        if (wishlistDTO == null)
            return null;
            
        Wishlist wishlist = new Wishlist();
        wishlist.setWishlistId(wishlistDTO.getWishlistId());
        wishlist.setWishlistDescription(wishlistDTO.getWishlistDescription());
        wishlist.setTotalValue(wishlistDTO.getTotalValue());
        wishlist.setPriority(wishlistDTO.getPriority());
        return wishlist;
    }

}
