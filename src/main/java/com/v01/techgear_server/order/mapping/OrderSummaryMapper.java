package com.v01.techgear_server.order.mapping;

import java.util.*;

import com.v01.techgear_server.order.model.OrderSummary;
import org.mapstruct.*;
import com.v01.techgear_server.utils.BaseMapper;
import com.v01.techgear_server.order.dto.OrderSummaryDTO;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class },
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderSummaryMapper extends BaseMapper<OrderSummary, OrderSummaryDTO> {
	@Override
	OrderSummaryDTO toDTO(OrderSummary entity);

	@Override
	OrderSummary toEntity(OrderSummaryDTO dto);

	@Override
	default List<OrderSummaryDTO> toDTOList(List<OrderSummary> entityList) {
		return Optional.ofNullable(entityList)
				.orElse(Collections.emptyList())
				.stream()
				.map(this::toDTO)
				.toList();
	}

	@Override
	default List<OrderSummary> toEntityList(List<OrderSummaryDTO> dtoList) {
		return Optional.ofNullable(dtoList)
				.orElse(Collections.emptyList())
				.stream()
				.map(this::toEntity)
				.toList();
	}
}


