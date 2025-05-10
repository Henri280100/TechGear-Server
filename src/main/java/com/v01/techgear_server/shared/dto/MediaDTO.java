package com.v01.techgear_server.shared.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.product.dto.ProductDetailDTO;
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
@Schema(description="Media DTO")
public class MediaDTO {
    @Schema(description = "Media ID", example = "123")
    @Positive(message = "Media ID must be a positive number")
    private Long id;

    @Schema(description = "Media filename")
    private String filename;

    @Schema(description = "Media url")
    private String mediaUrl;

    @Schema(description = "Image dimensions")
    private MediaDimensions dimensions;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Product details associated with the media")
    private ProductDetailDTO productDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Media Dimensions")
    public static class MediaDimensions {
        @Schema(description = "Width of the media", example = "1920")
        private Integer width;

        @Schema(description = "Height of the media", example = "1080")
        private Integer height;
    }
}
