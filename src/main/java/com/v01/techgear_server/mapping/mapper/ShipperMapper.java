package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
        OrderMapper.class,
        ShipperRatingMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShipperMapper extends BaseMapper<Shipper, ShipperDTO> {

    @Override
    @Mapping(target = "shipperId", source = "shipperId")
    @Mapping(target = "shipperName", source = "shipperName")
    @Mapping(target = "shipperRatings.id", ignore = true)
    @Mapping(target = "order.id", ignore = true)
    @Mapping(target = "shipperRatings", source = "shipperRatings", qualifiedByName = "toShipperRatingDTO")
    ShipperDTO toDTO(Shipper shipper);

    @Override
    @Mapping(target = "shipperId", source = "shipperId")
    @Mapping(target = "shipperName", source = "shipperName")
    @Mapping(target = "shipperRatings.id", ignore = true)
    @Mapping(target = "order.id", ignore = true)
    @Mapping(target = "shipperRatings", source = "shipperRatings", qualifiedByName = "toShipperRating")
    Shipper toEntity(ShipperDTO shipperDTO);

    @Override
    default List<ShipperDTO> toDTOList(List<Shipper> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<Shipper> toEntityList(List<ShipperDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }

}
