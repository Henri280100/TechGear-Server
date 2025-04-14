package com.v01.techgear_server.payment.mapping;

import java.util.*;

import com.v01.techgear_server.payment.model.PaymentMethod;
import com.v01.techgear_server.payment.dto.PaymentMethodDTO;
import com.v01.techgear_server.user.mapping.AccountDetailsMapper;
import org.mapstruct.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
        AccountDetailsMapper.class,
})
public interface PaymentMethodMapper extends BaseMapper<PaymentMethod, PaymentMethodDTO> {

    @Override
    @Mapping(target = "paymentStatus", source = "status")
    @Mapping(target = "paymentMethodId", ignore = true)
    PaymentMethodDTO toDTO(PaymentMethod entity);

    @Override
    @Mapping(target = "stripePaymentMethodId", ignore = true)
    @Mapping(target = "status", source = "paymentStatus")
    @Mapping(target = "id", ignore = true)
    PaymentMethod toEntity(PaymentMethodDTO dto);

    @Override
    default List<PaymentMethodDTO> toDTOList(List<PaymentMethod> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<PaymentMethod> toEntityList(List<PaymentMethodDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }
}
