package com.v01.techgear_server.mapping.mapper;

import org.mapstruct.*;
import java.util.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
        ProductMapper.class,
        ImageMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductSpecificationMapper extends BaseMapper<ProductSpecification, ProductSpecificationDTO> {

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "specName", source = "specName")
    @Mapping(target = "specValue", source = "specValue")
    @Mapping(target = "icon", source = "icon")
    @Mapping(target = "productDetails.id", ignore = true)
    @Mapping(target = "productDetails", qualifiedByName = "toProductDetailsDTO")
    @Mapping(target = "specImage.id", ignore = true)
    @Mapping(target = "specImage", source = "specImage", qualifiedByName = "toImageDTO")
    ProductSpecificationDTO toDTO(ProductSpecification productSpecification);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "specName", source = "specName")
    @Mapping(target = "specValue", source = "specValue")
    @Mapping(target = "icon", source = "icon")
    @Mapping(target = "productDetails.id", ignore = true)
    @Mapping(target = "productDetails", qualifiedByName = "toProductDetails")
    @Mapping(target = "specImage.id", ignore = true)
    @Mapping(target = "specImage", source = "specImage", qualifiedByName = "toImage")
    ProductSpecification toEntity(ProductSpecificationDTO productSpecificationDTO);

    @Override
    default List<ProductSpecificationDTO> toDTOList(List<ProductSpecification> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<ProductSpecification> toEntityList(List<ProductSpecificationDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }

}
