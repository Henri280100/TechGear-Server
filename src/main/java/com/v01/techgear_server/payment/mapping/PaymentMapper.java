package com.v01.techgear_server.payment.mapping;

import java.util.*;

import com.v01.techgear_server.payment.model.Payment;
import com.v01.techgear_server.order.mapping.OrderSummaryMapper;
import com.v01.techgear_server.payment.dto.PaymentDTO;
import com.v01.techgear_server.user.mapping.AccountDetailsMapper;
import com.v01.techgear_server.utils.*;

import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
		AccountDetailsMapper.class,
		OrderSummaryMapper.class
})
public interface PaymentMapper extends BaseMapper<Payment, PaymentDTO> {

	@Override
	@Mapping(target = "userAccountDetails", source = "accountDetails", ignore = true)
	@Mapping(target = "status", source = "paymentStatus")
	@Mapping(target = "paymentInvoice", source = "invoice", ignore = true)
	@Mapping(target = "paymentDateTime", source = "paymentDate")
	PaymentDTO toDTO(Payment entity);

	@Override
	@Mapping(target = "paymentStatus", source = "status")
	@Mapping(target = "paymentDate", source = "paymentDateTime")
	@Mapping(target = "invoice", source = "paymentInvoice", ignore = true)
	@Mapping(target = "accountDetails", source = "userAccountDetails", ignore = true)
	Payment toEntity(PaymentDTO dto);

	@Override
	default List<PaymentDTO> toDTOList(List<Payment> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<Payment> toEntityList(List<PaymentDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}

}