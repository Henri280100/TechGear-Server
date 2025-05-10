package com.v01.techgear_server.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Image Dimensions")
public class ImageDimensionsDTO {
	@Schema(description = "Width of the image", example = "1920")
	private Integer width;

	@Schema(description = "Height of the image", example = "1080")
	private Integer height;
}

