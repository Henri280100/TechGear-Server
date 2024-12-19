package com.v01.techgear_server.mapping.mapper;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.v01.techgear_server.dto.OrderItemsDTO;
import com.v01.techgear_server.model.OrderItems;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        OrderMapper.class,
        ProductMapper.class,
})
public interface OrderItemsMapper {
    
    @Mapping(source = "orderItemsId", target = "id")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    @Mapping(target = "order.orderId", qualifiedByName = "orderId")
    @Mapping(target = "product.productId", qualifiedByName = "productId")
    OrderItemsDTO toDto(OrderItems orderItems);

    @Mapping(source = "id", target = "orderItemsId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "price", target = "price")
    @Mapping(target = "order.orderId", qualifiedByName = "orderId")
    @Mapping(target = "product.productId", qualifiedByName = "productId")
    OrderItems toEntity(OrderItemsDTO orderItemsDTO);

    // Use default methods for list conversions
    default List<OrderItemsDTO> toDtoList(List<OrderItems> orderItems) {
        return orderItems == null ? Collections.emptyList() 
            : orderItems.stream()
                .map(this::toDto)
                .toList();
    }

    default List<OrderItems> toEntityList(List<OrderItemsDTO> orderItemsDTOs) {
        return orderItemsDTOs == null ? Collections.emptyList()
            : orderItemsDTOs.stream()
                .map(this::toEntity)
                .toList();
    }
}
