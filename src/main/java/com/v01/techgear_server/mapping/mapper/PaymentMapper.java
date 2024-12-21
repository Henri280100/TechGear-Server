package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.dto.*;

import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", uses = {
        AccountDetailsMapper.class,
        OrderSummaryMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface PaymentMapper extends BaseMapper<Payment, PaymentDTO> {

    @Override
    @Mapping(target = "paymentId", source = "paymentId", ignore = true)
    @Mapping(target = "stripePaymentIntentId", source = "stripePaymentIntentId", ignore = true)
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    @Mapping(target = "paymentDate", source = "paymentDate")
    @Mapping(target = "paymentAmount", source = "paymentAmount")
    @Mapping(target = "invoice.invoiceId", ignore = true)
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "toInvoiceDTO")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "toPaymentMethodDTO")
    @Mapping(target = "paymentMethod.paymentMethodId", ignore = true)
    @Mapping(target = "accountDetails", source = "accountDetails", qualifiedByName = "toAccountDetailsDTO")
    @Mapping(target = "accountDetails.accountDetailsId", ignore = true)
    @Mapping(target = "orderSummary", source = "orderSummary", qualifiedByName = "toOrderSummaryDTO")
    @Mapping(target = "orderSummary.orderSummaryId", ignore = true)
    PaymentDTO toDTO(Payment entity);

    @Override
    @Mapping(target = "paymentId", source = "paymentId", ignore = true)
    @Mapping(target = "stripePaymentIntentId", source = "stripePaymentIntentId", ignore = true)
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    @Mapping(target = "paymentDate", source = "paymentDate")
    @Mapping(target = "paymentAmount", source = "paymentAmount")
    @Mapping(target = "invoice.invoiceId", ignore = true)
    @Mapping(target = "invoice", source = "invoice", qualifiedByName = "toInvoice")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "toPaymentMethod")
    @Mapping(target = "paymentMethod.paymentMethodId", ignore = true)
    @Mapping(target = "accountDetails", source = "accountDetails", qualifiedByName = "toAccountDetails")
    @Mapping(target = "accountDetails.accountDetailsId", ignore = true)
    @Mapping(target = "orderSummary", source = "orderSummary", qualifiedByName = "toOrderSummary")
    @Mapping(target = "orderSummary.orderSummaryId", ignore = true)
    Payment toEntity(PaymentDTO dto);

    @Override
    default List<PaymentDTO> toDTOList(List<Payment> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<Payment> toEntityList(List<PaymentDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }

}