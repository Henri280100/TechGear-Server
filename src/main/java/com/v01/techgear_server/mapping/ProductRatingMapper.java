package com.v01.techgear_server.mapping;

import java.util.*;

import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
        ProductMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductRatingMapper extends BaseMapper<ProductRating, ProductRatingDTO> {
    // target to dto, source to entity
    @Override
    @Mapping(target = "productReviewImage", ignore = true)
    @Mapping(target = "productRating", source = "rating")
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "productComments", source = "comments")
    @Mapping(target = "accountId", ignore = true)
    ProductRatingDTO toDTO(ProductRating productRating);

    @Override
    @Mapping(target = "reviewImage", ignore = true)
    @Mapping(target = "rating", source = "productRating")
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "comments", source = "productComments")
    @Mapping(target = "accountDetails", ignore = true)
    ProductRating toEntity(ProductRatingDTO productRatingDTO);

    @Override
    default List<ProductRatingDTO> toDTOList(List<ProductRating> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    default List<ProductRating> toEntityList(List<ProductRatingDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream()
                .map(this::toEntity)
                .toList();
    }


}
