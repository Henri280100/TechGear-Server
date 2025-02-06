package com.v01.techgear_server.mapping;

import java.util.*;

import org.mapstruct.*;
import com.v01.techgear_server.model.OrderSummary;
import com.v01.techgear_server.utils.BaseMapper;
import com.v01.techgear_server.dto.OrderSummaryDTO;

@Mapper(componentModel = "spring", uses = {
		OrderItemsMapper.class
}, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderSummaryMapper extends BaseMapper<OrderSummary, OrderSummaryDTO> {
	@Override
	OrderSummaryDTO toDTO(OrderSummary entity);

	@Override
	OrderSummary toEntity(OrderSummaryDTO dto);

	@Override
	default List<OrderSummaryDTO> toDTOList(List<OrderSummary> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
	}

	@Override
	default List<OrderSummary> toEntityList(List<OrderSummaryDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
	}

}
