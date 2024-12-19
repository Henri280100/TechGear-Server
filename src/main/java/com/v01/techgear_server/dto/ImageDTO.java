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

    @Schema(description = "Image URL")
    private String imageUrl;

    @Schema(description = "Image display name")
    private String fileName;

    @Schema(description = "Image type")
    private ImageTypes imageType;

    @Schema(description = "Image dimensions")
    private ImageDimensions dimensions;

    @Data
    @Builder
    public static class ImageDimensions {
        private Integer width;
        private Integer height;
    }

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}
