package com.v01.techgear_server.mapping.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.v01.techgear_server.dto.ImageDTO;
import com.v01.techgear_server.model.Image;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        AccountDetailsMapper.class,
        ProductMapper.class,
        ProductRatingMapper.class,
        ProductSpecificationMapper.class,
        WishlistMapper.class,
        WishlistItemsMapper.class
})
public interface ImageMapper extends BaseMapper<Image, ImageDTO> {
    // Detailed Mapping with Custom Configurations
    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "contentType", source = "contentType", qualifiedByName = "validateContentType")
    @Mapping(target = "data", source = "data", qualifiedByName = "processImageData")
    @Mapping(target = "fileSize", source = "fileSize")
    @Mapping(target = "width", source = "width")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    @Mapping(target = "uploadedBy", source = "uploadedBy")
    @Mapping(target = "imageTypes", source = "imageTypes")
    ImageDTO toDTO(Image image);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "contentType", source = "contentType", qualifiedByName = "validateContentType")
    @Mapping(target = "data", source = "data", qualifiedByName = "processImageData")
    @Mapping(target = "fileSize", source = "fileSize")
    @Mapping(target = "width", source = "width")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "lastModifiedAt", source = "lastModifiedAt")
    @Mapping(target = "uploadedBy", source = "uploadedBy")
    @Mapping(target = "imageTypes", source = "imageTypes")
    Image toEntity(ImageDTO imageDTO);
}
