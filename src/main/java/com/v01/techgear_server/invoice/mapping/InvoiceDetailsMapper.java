package com.v01.techgear_server.invoice.mapping;

import java.util.*;
import org.mapstruct.*;

import com.v01.techgear_server.invoice.dto.InvoiceDetailsDTO;
import com.v01.techgear_server.invoice.model.InvoiceDetails;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface InvoiceDetailsMapper extends BaseMapper<InvoiceDetails, InvoiceDetailsDTO> {

    @Override
    @Mapping(source = "invoice", target = "invoice.invoiceId", ignore = true)
    @Mapping(source = "product", target = "product.id", ignore = true)
    @Mapping(source = "discount", target = "discount.discountId", ignore = true)
    @Mapping(source = "lineTotal", target = "lineTotal")
    InvoiceDetailsDTO toDTO(InvoiceDetails entity);

    @Override
    default List<InvoiceDetailsDTO> toDTOList(List<InvoiceDetails> entityList) {
        if (entityList == null)
            return Collections.emptyList();

        return entityList.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Mapping(source = "invoice.invoiceId", target = "invoice", ignore = true)
    @Mapping(source = "product.id", target = "product", ignore = true)
    @Mapping(source = "discount.discountId", target = "discount", ignore = true)
    InvoiceDetails toEntity(InvoiceDetailsDTO dto);

    @Override
    default List<InvoiceDetails> toEntityList(List<InvoiceDetailsDTO> dtoList) {
        if (dtoList == null)
            return Collections.emptyList();

        return dtoList.stream()
                .map(this::toEntity)
                .toList();
    }

}
