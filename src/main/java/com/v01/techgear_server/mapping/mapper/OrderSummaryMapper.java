package com.v01.techgear_server.mapping.mapper;

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
    @Mapping(target = "orderSummaryId", source = "orderSummaryId")
    @Mapping(target = "orderDate", source = "orderDate")
    @Mapping(target = "orderStatus", source = "orderStatus")
    @Mapping(target = "subTotal", source = "subTotal")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "orderItems", source = "toOrderItemsDTO")
    OrderSummaryDTO toDTO(OrderSummary entity);

    @Override
    @Mapping(target = "orderSummaryId", source = "orderSummaryId")
    @Mapping(target = "orderDate", source = "orderDate")
    @Mapping(target = "orderStatus", source = "orderStatus")
    @Mapping(target = "subTotal", source = "subTotal")
    @Mapping(target = "totalAmount", source = "totalAmount")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "orderItems", source = "toOrderItems")
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
