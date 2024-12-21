package com.v01.techgear_server.mapping.mapper;

import java.util.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.dto.ImageDTO.ImageDimensions;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ImageMapper extends BaseMapper<Image, ImageDTO> {

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "imageType", source = "imageType")
    @Mapping(target = "dimensions", qualifiedByName = "toImageDimensionsDTO")
    @Mapping(target = "createdAt", source = "createdAt")
    ImageDTO toDTO(Image image);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "imageType", source = "imageType")
    @Mapping(target = "dimensions", qualifiedByName = "toImageDimensions")
    @Mapping(target = "createdAt", source = "createdAt")
    Image toEntity(ImageDTO imageDTO);

    // Dimensions Mapping
    @Named("toImageDimensionsDTO")
    default ImageDTO.ImageDimensions mapToDimensionsDTO(ImageDimensions dimensions) {
        if (dimensions == null) {
            return null;
        }

        return ImageDTO.ImageDimensions.builder()
                .width(dimensions.getWidth())
                .height(dimensions.getHeight())
                .build();
    }

    @Named("toImageDimensions")
    default ImageDimensions mapToDimensions(ImageDTO.ImageDimensions dimensionsDTO) {
        if (dimensionsDTO == null) {
            return null;
        }

        ImageDimensions dimensions = new ImageDimensions();
        dimensions.setWidth(dimensionsDTO.getWidth());
        dimensions.setHeight(dimensionsDTO.getHeight());
        return dimensions;
    }

    // Bulk mapping methods
    @Override
    default List<ImageDTO> toDTOList(List<Image> images) {
        if (images == null) {
            return Collections.emptyList();
        }

        return images.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    default List<Image> toEntityList(List<ImageDTO> imageDTOs) {
        if (imageDTOs == null) {
            return Collections.emptyList();
        }

        return imageDTOs.stream()
                .map(this::toEntity)
                .toList();
    }

}