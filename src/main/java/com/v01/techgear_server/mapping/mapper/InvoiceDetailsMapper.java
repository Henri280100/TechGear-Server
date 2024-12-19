package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;

import com.v01.techgear_server.dto.InvoiceDetailsDTO;
import com.v01.techgear_server.model.InvoiceDetails;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy=NullValuePropertyMappingStrategy.IGNORE,
uses={
    InvoiceMapper.class,
    ProductMapper.class,
    DiscountMapper.class,
}, nullValueIterableMappingStrategy=NullValueMappingStrategy.RETURN_NULL)
public interface InvoiceDetailsMapper extends BaseMapper<InvoiceDetails, InvoiceDetailsDTO>{

    @Override
    @Mapping(source="id", target="invoiceDetailsId")
    @Mapping(source="invoice", target="invoice")
    @Mapping(source="product", target="product")
    @Mapping(source="discount", target="discount")
    @Mapping(source="lineTotal", target="lineTotal")
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
    @Mapping(source="id", target="invoiceDetailsId")
    @Mapping(source="invoice", target="invoice")
    @Mapping(source="product", target="product")
    @Mapping(source="discount", target="discount")
    @Mapping(source="lineTotal", target="lineTotal")
    InvoiceDetails toEntity(InvoiceDetailsDTO dto);

    @Override
    default List<InvoiceDetails> toEntityList(List<InvoiceDetailsDTO> dtoList) {
        if (dtoList == null) return Collections.emptyList();

        return dtoList.stream()
                .map(this::toEntity)
                .toList();
    }
    

}
