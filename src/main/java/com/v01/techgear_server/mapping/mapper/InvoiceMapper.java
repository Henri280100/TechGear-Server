package com.v01.techgear_server.mapping.mapper;

import com.v01.techgear_server.utils.*;
import org.mapstruct.Mapper;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvoiceMapper extends BaseMapper<Invoice, InvoiceDTO> {
    
    @Override
    @Mapping(source = "invoiceId", target = "invoiceId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "invoiceNumber", target = "invoiceNumber")
    @Mapping(source = "issueDate", target = "issueDate")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "invoiceDetails", target = "invoiceDetails")
    InvoiceDTO toDTO(Invoice entity);

    @Override
    @Mapping(source = "invoiceId", target = "invoiceId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "invoiceNumber", target = "invoiceNumber")
    @Mapping(source = "issueDate", target = "issueDate")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "invoiceDetails", target = "invoiceDetails")
    Invoice toEntity(InvoiceDTO dto);

    @Override
    default List<InvoiceDTO> toDTOList(List<Invoice> entityList) {
        if (entityList == null)
            return Collections.emptyList();

        return entityList.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    default List<Invoice> toEntityList(List<InvoiceDTO> dtoList) {
        if (dtoList == null)
            return Collections.emptyList();

        return dtoList.stream()
                .map(this::toEntity)
                .toList();
    }
}
