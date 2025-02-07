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
    @Mapping(target = "status", source = "orderItemStatus")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "orderSummaryId", source = "orderSummary.orderSummaryId", ignore = true)
    @Mapping(target = "orderId", source = "order.orderId", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "itemsUnitPrice", source = "price", ignore = true)
    @Mapping(target = "itemsQuantity", source = "quantity", ignore = true)
    @Mapping(target = "id", source = "orderItemsId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OrderItemsDTO toDTO(OrderItems orderItems);

    @Override
    @Mapping(target = "quantity", source = "itemsQuantity")
    @Mapping(target = "product.name", source = "productName")
    @Mapping(target = "price", source = "itemsUnitPrice")
    @Mapping(target = "orderSummary", source = "orderSummaryId", ignore = true)
    @Mapping(target = "orderItemsId", source = "id", ignore = true)
    @Mapping(target = "orderItemStatus", source = "status")
    @Mapping(target = "order", source = "orderId", ignore = true)
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
