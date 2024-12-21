package com.v01.techgear_server.mapping.mapper;

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
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "orderStatus", target = "orderStatus")
    @Mapping(source = "orderDate", target = "orderDate")
    @Mapping(target = "accountDetails.accountDetailsId", ignore = true)
    @Mapping(source = "accountDetails", target = "toAccountDetailsDTO")
    OrderDTO toDTO(Order entity);

    @Override
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "orderStatus", target = "orderStatus")
    @Mapping(source = "orderDate", target = "orderDate")
    @Mapping(target = "accountDetails.accountDetailsId", ignore = true)
    @Mapping(source = "accountDetails", target = "toAccountDetails")
    Order toEntity(OrderDTO dto);

    @Override
    default List<OrderDTO> toDTOList(List<Order> entityList) {
        if (entityList == null)
            return Collections.emptyList();

        return entityList.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    default List<Order> toEntityList(List<OrderDTO> dtoList) {
        if (dtoList == null)
            return Collections.emptyList();

        return dtoList.stream()
                .map(this::toEntity)
                .toList();
    }
    
    
}
