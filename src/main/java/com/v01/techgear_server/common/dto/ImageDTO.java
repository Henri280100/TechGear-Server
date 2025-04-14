package com.v01.techgear_server.common.dto;

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

    @Schema(description = "Image URL", example = "https://example.com/images/sample.jpg")
    private String imageUrl;

    @Schema(description = "Image public ID", example = "123124")
    private String publicId;

    @Schema(description = "Image type", example = "PNG")
    private ImageTypes type;

    @Schema(description = "Image dimensions")
    private ImageDimensionsDTO dimensions;

    @Schema(description = "Creation timestamp", example = "2024-02-01T12:00:00")
    private LocalDateTime createdAt;
}

