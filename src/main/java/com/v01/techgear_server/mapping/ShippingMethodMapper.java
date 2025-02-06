package com.v01.techgear_server.mapping;

import java.util.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.dto.*;

import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ShippingMethodMapper extends BaseMapper<ShippingMethod, ShippingMethodDTO> {
    @Override
    ShippingMethodDTO toDTO(ShippingMethod entity);

    @Override
    @Mapping(target = "shippingDetails", ignore = true)
    @Mapping(target = "deliveryTime", ignore = true)
    ShippingMethod toEntity(ShippingMethodDTO dto);

    @Override
    default List<ShippingMethodDTO> toDTOList(List<ShippingMethod> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<ShippingMethod> toEntityList(List<ShippingMethodDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }

}