package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.dto.*;

import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        ShippingMethodMapper.class
})
public interface ShippingDetailsMapper extends BaseMapper<ShippingDetails, ShippingDetailsDTO> {

    @Override
    @Mapping(target = "shippingDetailsId", source = "shippingDetailsId", ignore = true)
    @Mapping(target = "addressLine1", source = "addressLine1")
    @Mapping(target = "addressLine2", source = "addressLine2")
    @Mapping(target = "city", source = "city")
    @Mapping(target = " state", source = "state")
    @Mapping(target = "postalCode", source = "postalCode")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "shippingDate", source = "shippingDate")
    @Mapping(target = "shippingMethod", source = "shippingMethod", qualifiedByName = "toShippingMethodDTO")
    @Mapping(target = "shippingMethod.shippingMethodId", source = "shippingMethod.shippingMethodId", ignore = true)
    ShippingDetailsDTO toDTO(ShippingDetails entity);

    @Override
    @Mapping(target = "shippingDetailsId", source = "shippingDetailsId", ignore = true)
    @Mapping(target = "addressLine1", source = "addressLine1")
    @Mapping(target = "addressLine2", source = "addressLine2")
    @Mapping(target = "city", source = "city")
    @Mapping(target = " state", source = "state")
    @Mapping(target = "postalCode", source = "postalCode")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "shippingDate", source = "shippingDate")
    @Mapping(target = "shippingMethod", source = "shippingMethod", qualifiedByName = "toShippingMethod")
    @Mapping(target = "shippingMethod.shippingMethodId", source = "shippingMethod.shippingMethodId", ignore = true)
    ShippingDetails toEntity(ShippingDetailsDTO dto);

    @Override
    default List<ShippingDetailsDTO> toDTOList(List<ShippingDetails> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<ShippingDetails> toEntityList(List<ShippingDetailsDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }

}