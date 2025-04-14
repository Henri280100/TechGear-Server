package com.v01.techgear_server.invoice.mapping;

import com.v01.techgear_server.invoice.dto.InvoiceDTO;
import com.v01.techgear_server.invoice.model.Invoice;
import com.v01.techgear_server.utils.*;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import java.util.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InvoiceMapper extends BaseMapper<Invoice, InvoiceDTO> {

    @Override
    InvoiceDTO toDTO(Invoice entity);

    @Override
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
