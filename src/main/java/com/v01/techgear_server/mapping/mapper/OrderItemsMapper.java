package com.v01.techgear_server.mapping.mapper;

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
    @Mapping(source = "orderItemsId", target = "id")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(target="orderItemStatus", source="orderItemStatus")
    @Mapping(target = "order.orderId", ignore = true)
    @Mapping(target = "product.productId", ignore = true)
    @Mapping(target= "order", qualifiedByName="toOrderDTO")
    @Mapping(target="product", qualifiedByName="toProductDTO")
    OrderItemsDTO toDTO(OrderItems orderItems);

    @Override
    @Mapping(source = "id", target = "orderItemsId")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(target="orderItemStatus", source="orderItemStatus")
    @Mapping(target = "order.orderId", ignore = true)
    @Mapping(target = "product.productId", ignore = true)
    @Mapping(target= "order", qualifiedByName="toOrder")
    @Mapping(target="product", qualifiedByName="toProduct")
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
