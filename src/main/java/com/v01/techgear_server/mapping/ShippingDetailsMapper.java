package com.v01.techgear_server.mapping;

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
	@Mapping(target = "shippingMethodDTO", ignore = true)
	@Mapping(target = "addressLineTwo", ignore = true)
	@Mapping(target = "addressLineOne", ignore = true)
	@Mapping(target = "shipperName", source = "username")
	ShippingDetailsDTO toDTO(ShippingDetails entity);

	@Override
	@Mapping(target = "shippingMethod", ignore = true)
	@Mapping(target = "shippingDate", ignore = true)
	@Mapping(target = "postalCode", ignore = true)
	@Mapping(target = "estimatedDeliveryDate", ignore = true)
	@Mapping(target = "addressLine2", ignore = true)
	@Mapping(target = "addressLine1", ignore = true)
	@Mapping(target = "username", source = "shipperName")
	@Mapping(target = "phoneNumber", ignore = true)
	@Mapping(target = "order", ignore = true)
	@Mapping(target = "email", ignore = true)
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