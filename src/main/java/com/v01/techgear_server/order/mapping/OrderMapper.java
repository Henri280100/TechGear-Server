package com.v01.techgear_server.order.mapping;

import com.v01.techgear_server.order.dto.OrderDTO;
import com.v01.techgear_server.order.model.Order;
import com.v01.techgear_server.user.mapping.AccountDetailsMapper;
import com.v01.techgear_server.utils.*;

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
