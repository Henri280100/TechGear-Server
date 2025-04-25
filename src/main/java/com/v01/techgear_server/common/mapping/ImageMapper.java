package com.v01.techgear_server.common.mapping;

import java.util.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.v01.techgear_server.common.dto.ImageDTO;
import com.v01.techgear_server.common.model.Image;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring")
public interface ImageMapper extends BaseMapper<Image, ImageDTO> {

    @Override
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "dimensions", ignore = true)
    ImageDTO toDTO(Image image);


    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "wishlistItems", ignore = true)
    @Mapping(target = "wishlist", ignore = true)
    @Mapping(target = "width", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productRating", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "imageTypes", ignore = true)
    @Mapping(target = "height", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "data", ignore = true)
    @Mapping(target = "contentType", ignore = true)
    @Mapping(target = "accountDetails", ignore = true)
    Image toEntity(ImageDTO imageDTO);

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