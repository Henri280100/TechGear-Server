package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;

import com.v01.techgear_server.dto.*;

import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
                ProductMapper.class })
public interface ProductDetailMapper extends BaseMapper<ProductDetail, ProductDetailDTO> {

        @Override
        @Mapping(target = "id", source = "id")
        @Mapping(target = "warranty", source = "warranty")
        @Mapping(target = "description", source = "description")
        @Mapping(target = "technicalSpecs", source = "technicalSpecifications")
        @Mapping(target = "product", qualifiedByName = "toProductDTO")
        ProductDetailDTO toDTO(ProductDetail productDetail);

        @Override
        @Mapping(target = "id", source = "id")
        @Mapping(target = "warranty", source = "warranty")
        @Mapping(target = "description", source = "description")
        @Mapping(target = "technicalSpecifications", source = "technicalSpecs")
        @Mapping(target = "product", qualifiedByName = "toProduct")
        ProductDetail toEntity(ProductDetailDTO productDetailDTO);

        @Override
        List<ProductDetailDTO> toDTOList(List<ProductDetail> productDetails);

        @Override
        List<ProductDetail> toEntityList(List<ProductDetailDTO> productDetailDTOs);
}
