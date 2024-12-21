package com.v01.techgear_server.mapping.mapper;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.v01.techgear_server.dto.ProductDTO;
import com.v01.techgear_server.model.Product;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        ImageMapper.class })
public interface ProductMapper extends BaseMapper<Product, ProductDTO> {

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "image", qualifiedByName = "toImageDTO")
    @Mapping(target = "category", source = "category")
    ProductDTO toDTO(Product product);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "availability", source = "availability")
    @Mapping(target = "image", qualifiedByName = "toImage")
    @Mapping(target = "category", source = "category")
    Product toEntity(ProductDTO productDTO);

    @Override
    default List<ProductDTO> toDTOList(List<Product> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<Product> toEntityList(List<ProductDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }

}
