package com.v01.techgear_server.shipping.mapping;

import java.util.*;

import com.v01.techgear_server.shipping.model.Shipper;
import com.v01.techgear_server.order.mapping.OrderMapper;
import com.v01.techgear_server.shipping.dto.ShipperDTO;
import org.mapstruct.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
		OrderMapper.class,
		ShipperRatingMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShipperMapper extends BaseMapper<Shipper, ShipperDTO> {

	@Override
	@Mapping(target = "shipperName", source = "shipperName")
	@Mapping(target = "shipperRatings", ignore = true)
	@Mapping(target = "orders", ignore = true)
	ShipperDTO toDTO(Shipper shipper);

	@Override
	@Mapping(target = "shipperName", source = "shipperName")
	@Mapping(target = "shipperRatings", ignore = true)
	@Mapping(target = "orders", ignore = true)
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
