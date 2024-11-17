package com.v01.techgear_server.dto;

import java.time.LocalDateTime;

import com.v01.techgear_server.enums.ImageTypes;
import com.v01.techgear_server.model.Image;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

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

    public static ImageDTO fromEntity(Image entity) {
        if (entity == null) {
            return null;
        }

        return ImageDTO.builder()
                .id(entity.getId())
                .imageUrl(entity.getImageUrl())
                .fileName(entity.getFileName())
                .contentType(entity.getContentType())
                .data(entity.getData())
                .fileSize(entity.getFileSize())
                .width(entity.getWidth())
                .height(entity.getHeight())
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .uploadedBy(entity.getUploadedBy())
                .imageTypes(entity.getImageTypes())
                .build();
    }

    public Image toEntity() {
        Image image = new Image();
        image.setId(this.id);
        image.setImageUrl(this.imageUrl);
        image.setFileName(this.fileName);
        image.setContentType(this.contentType);
        image.setData(this.data);
        image.setFileSize(this.fileSize);
        image.setWidth(this.width);
        image.setHeight(this.height);
        image.setCreatedAt(this.createdAt);
        image.setLastModifiedAt(this.lastModifiedAt);
        image.setUploadedBy(this.uploadedBy);
        image.setImageTypes(this.imageTypes);
        return image;
    }

    public static ImageDTO fromBytes(byte[] data) {
        ImageDTO imageDTO = ImageDTO.builder()
                .data(data)
                .build();

        return imageDTO;
    }
}
