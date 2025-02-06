package com.v01.techgear_server.mapping;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.v01.techgear_server.dto.OrderItemsDTO;
import com.v01.techgear_server.model.OrderItems;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
		OrderMapper.class,
		ProductMapper.class,
})
public interface OrderItemsMapper extends BaseMapper<OrderItems, OrderItemsDTO> {

	@Override
	@Mapping(target = "status", source = "orderItemStatus", ignore = true)
	@Mapping(target = "productId", ignore = true)
	@Mapping(target = "orderId", ignore = true)
	@Mapping(target = "itemsUnitPrice", source = "price", ignore = true)
	@Mapping(target = "itemsQuantity", source = "quantity", ignore = true)
	@Mapping(target = "id", ignore = true)
	OrderItemsDTO toDTO(OrderItems orderItems);

	@Override
	@Mapping(target = "quantity", source = "itemsQuantity", ignore = true)
	@Mapping(target = "product.productId", source = "productId", ignore = true)
	@Mapping(target = "price", source = "itemsUnitPrice", ignore = true)
	@Mapping(target = "orderSummary",  ignore = true)
	@Mapping(target = "orderItemsId", ignore = true)
	@Mapping(target = "orderItemStatus", ignore = true)
	@Mapping(target = "order", ignore = true)
	OrderItems toEntity(OrderItemsDTO orderItemsDTO);

	// Use default methods for list conversions
	@Override
	default List<OrderItemsDTO> toDTOList(List<OrderItems> orderItems) {
		return orderItems == null ? Collections.emptyList()
				: orderItems.stream()
				            .map(this::toDTO)
				            .toList();
	}

	@Override
	default List<OrderItems> toEntityList(List<OrderItemsDTO> orderItemsDTOs) {
		return orderItemsDTOs == null ? Collections.emptyList()
				: orderItemsDTOs.stream()
				                .map(this::toEntity)
				                .toList();
	}
}
