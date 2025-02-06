package com.v01.techgear_server.mapping;

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
})
public interface PaymentMapper extends BaseMapper<Payment, PaymentDTO> {

	@Override
	@Mapping(target = "userAccountDetails", source = "accountDetails", ignore = true)
	@Mapping(target = "summaryDTO", source = "orderSummary", ignore = true)
	@Mapping(target = "status", source = "paymentStatus")
	@Mapping(target = "paymentInvoice", source = "invoice", ignore = true)
	@Mapping(target = "paymentDateTime", source = "paymentDate")
	PaymentDTO toDTO(Payment entity);

	@Override
	@Mapping(target = "paymentStatus", source = "status")
	@Mapping(target = "paymentDate", source = "paymentDateTime")
	@Mapping(target = "orderSummary", source = "summaryDTO", ignore = true)
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