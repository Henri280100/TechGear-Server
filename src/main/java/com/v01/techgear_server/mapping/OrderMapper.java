package com.v01.techgear_server.mapping;

import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;

import java.util.*;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {
		AccountDetailsMapper.class
}, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper extends BaseMapper<Order, OrderDTO> {

	@Override
	OrderDTO toDTO(Order entity);

	@Override
	Order toEntity(OrderDTO dto);

	@Override
	default List<OrderDTO> toDTOList(List<Order> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<Order> toEntityList(List<OrderDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}


}
