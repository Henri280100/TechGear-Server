package com.v01.techgear_server.product.mapping;

import com.v01.techgear_server.product.dto.ProductCategoryDTO;
import com.v01.techgear_server.product.model.ProductCategory;
import com.v01.techgear_server.utils.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper extends BaseMapper<ProductCategory, ProductCategoryDTO> {
    @Override
    @Mapping(target = "productCategoryName", source = "categoryName")
    @Mapping(target = "productCategoryImage", source = "categoryImage")
    @Mapping(target = "productCategoryId", source = "id")
    ProductCategoryDTO toDTO(ProductCategory productCategory);

    @Override
    @Mapping(target = "id", source = "productCategoryId")
    @Mapping(target = "categoryName", source = "productCategoryName")
    @Mapping(target = "categoryImage", source = "productCategoryImage")
    ProductCategory toEntity(ProductCategoryDTO productCategoryDTO);

    @Override
    default List<ProductCategoryDTO> toDTOList(List<ProductCategory> productCategories) {
        return productCategories == null ? Collections.emptyList() : productCategories.stream().map(this::toDTO).toList();
    }

    @Override
    default List<ProductCategory> toEntityList(List<ProductCategoryDTO> productCategoryDTOs) {
        return productCategoryDTOs == null ? Collections.emptyList() : productCategoryDTOs.stream().map(this::toEntity).toList();
    }
}
