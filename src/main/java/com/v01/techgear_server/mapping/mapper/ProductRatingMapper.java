package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel="spring", uses = {
    ProductMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductRatingMapper extends BaseMapper<ProductRating, ProductRatingDTO>{
    @Override
    @Mapping(target = "productRatingId", source = "productRatingId")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "ratingDate", source = "ratingDate")
    @Mapping(target = "product.id", ignore=true)
    @Mapping(target = "product", qualifiedByName = "toProductDTO")
    ProductRatingDTO toDTO(ProductRating productRating);
    
    @Override
    @Mapping(target = "productRatingId", source = "productRatingId")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "ratingDate", source = "ratingDate")
    @Mapping(target = "product.id", ignore=true)
    @Mapping(target = "product", qualifiedByName = "toProduct")
    ProductRating toEntity(ProductRatingDTO productRatingDTO);

    @Override
    default List<ProductRatingDTO> toDTOList(List<ProductRating> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<ProductRating> toEntityList(List<ProductRatingDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }

    
}
