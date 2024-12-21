package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
        AccountDetailsMapper.class,
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMethodMapper extends BaseMapper<PaymentMethod, PaymentMethodDTO> {

    @Override
    @Mapping(target = "paymentMethodId", source = "paymentMethodId", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "lastFourDigits", source = "lastFourDigits")
    @Mapping(target = "cardBrand", source = "cardBrand")
    @Mapping(target = "expirationDate", source = "expirationDate")
    @Mapping(target = "isDefault", source = "isDefault")
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    @Mapping(target = "accountDetails", source = "accountDetails", qualifiedByName = "toAccountDetailsDTO")
    @Mapping(target = "accountDetails.accountDetailsId", ignore = true)
    PaymentMethodDTO toDTO(PaymentMethod entity);

    @Override
    @Mapping(target = "paymentMethodId", source = "paymentMethodId", ignore = true)
    @Mapping(target = "type", source = "type")
    @Mapping(target = "lastFourDigits", source = "lastFourDigits")
    @Mapping(target = "cardBrand", source = "cardBrand")
    @Mapping(target = "expirationDate", source = "expirationDate")
    @Mapping(target = "isDefault", source = "isDefault")
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    @Mapping(target = "accountDetails", source = "accountDetails", qualifiedByName = "toAccountDetails")
    @Mapping(target = "accountDetails.accountDetailsId", ignore = true)
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
