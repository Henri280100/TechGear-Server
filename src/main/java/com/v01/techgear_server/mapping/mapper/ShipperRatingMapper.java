package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.dto.*;

@Mapper(componentModel = "spring", uses = {
        ShipperMapper.class,
        OrderMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShipperRatingMapper extends BaseMapper<ShipperRating, ShipperRatingDTO> {
    
    @Override
    @Mapping(target = "shipperRatingId", source = "shipperRatingId")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "ratingDate", source = "ratingDate")
    @Mapping(target = "shipper.id", ignore = true)
    @Mapping(target = "shipper", qualifiedByName = "toShipperDTO")
    ShipperRatingDTO toDTO(ShipperRating shipperRating);

    @Override
    @Mapping(target = "shipperRatingId", source = "shipperRatingId")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "ratingDate", source = "ratingDate")
    @Mapping(target = "shipper.id", ignore = true)
    @Mapping(target = "shipper", qualifiedByName = "toShipper")
    ShipperRating toEntity(ShipperRatingDTO shipperRatingDTO);

    @Override
    default List<ShipperRatingDTO> toDTOList(List<ShipperRating> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<ShipperRating> toEntityList(List<ShipperRatingDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }
}
