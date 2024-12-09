package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.ImageTypes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Image Data Transfer Object")
public class ImageDTO {

    @Schema(description = "Image ID", example = "123")
    @Positive(message = "Image ID must be a positive number")
    private Long id;

    @Schema(description = "Image URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Image file name")
    private String fileName;

    @Schema(description = "Image content type")
    private String contentType;

    @Schema(description = "Image data")
    private byte[] data;

    @Schema(description = "Image file size")
    private Long fileSize;

    @Schema(description = "Image width")
    private Integer width;

    @Schema(description = "Image height")
    private Integer height;

    @Schema(description = "Image creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Image last modified timestamp")
    private LocalDateTime lastModifiedAt;

    @Schema(description = "Image uploaded by")
    private String uploadedBy;

    @Schema(name = "Image type")
    private ImageTypes imageTypes;

    @Schema(description = "Account details associated with the image")
    private AccountDetailsDTO accountDetailsDTO;

    @Schema(description = "Product associated with the image")
    private ProductDTO productDTO;

    @Schema(description = "Product rating associated with the image")
    private ProductRatingDTO productRatingDTO;

    @Schema(description = "Product specification associated with the image")
    private ProductSpecificationDTO productSpecificationDTO;

    @Schema(description = "Wishlist associated with the image")
    private WishlistDTO wishlistDTO;

    @Schema(description = "Wishlist item associated with the image")
    private WishlistItemsDTO wishlistItemsDTO;
}
