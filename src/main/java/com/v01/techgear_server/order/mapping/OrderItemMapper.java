package com.v01.techgear_server.order.mapping;

import com.v01.techgear_server.order.dto.OrderItemsDTO;
import com.v01.techgear_server.order.model.OrderItem;
import com.v01.techgear_server.product.mapping.ProductMapper;
import com.v01.techgear_server.utils.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        OrderMapper.class,
        ProductMapper.class,
})
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemsDTO> {

    @Override
    @Mapping(target = "orderSummaryId", ignore = true)
    @Mapping(target = "status", source = "orderItemStatus")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "orderId", source = "order.orderId", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "itemsUnitPrice", source = "price", ignore = true)
    @Mapping(target = "itemsQuantity", source = "quantity", ignore = true)
    @Mapping(target = "id", source = "orderItemsId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OrderItemsDTO toDTO(OrderItem orderItems);

    @Override
    @Mapping(target = "quantity", source = "itemsQuantity")
    @Mapping(target = "product.name", source = "productName")
    @Mapping(target = "price", source = "itemsUnitPrice")
    @Mapping(target = "orderItemsId", source = "id", ignore = true)
    @Mapping(target = "orderItemStatus", source = "status")
    @Mapping(target = "order", source = "orderId", ignore = true)
    OrderItem toEntity(OrderItemsDTO orderItemsDTO);

    // Use default methods for list conversions
    @Override
    default List<OrderItemsDTO> toDTOList(List<OrderItem> orderItems) {
        return orderItems == null ? Collections.emptyList()
                : orderItems.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    default List<OrderItem> toEntityList(List<OrderItemsDTO> orderItemsDTOs) {
        return orderItemsDTOs == null ? Collections.emptyList()
                : orderItemsDTOs.stream()
                .map(this::toEntity)
                .toList();
    }
}
