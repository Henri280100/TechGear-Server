package com.v01.techgear_server.mapping.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.v01.techgear_server.dto.DiscountDTO;
import com.v01.techgear_server.model.Discount;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DiscountMapper extends BaseMapper<Discount, DiscountDTO> {
    @Override
    @Mapping(target = "discountId", source = "discountId")
    @Mapping(target = "discountPercentage", source = "discountPercentage")
    @Mapping(target = "discountCode", source = "discountCode")
    @Mapping(target = "discountName", source = "discountName")
    @Mapping(target = "isDiscountActive", source = "isDiscountActive")
    @Mapping(target = "discountType", source = "discountType")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "expiryDate", source = "expiryDate")
    DiscountDTO toDTO(Discount entity);

    @Override
    @Mapping(target = "discountId", source = "discountId")
    @Mapping(target = "discountPercentage", source = "discountPercentage")
    @Mapping(target = "discountCode", source = "discountCode")
    @Mapping(target = "discountName", source = "discountName")
    @Mapping(target = "isDiscountActive", source = "isDiscountActive")
    @Mapping(target = "discountType", source = "discountType")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "expiryDate", source = "expiryDate")
    Discount toEntity(DiscountDTO dto);

    @Override
    default List<DiscountDTO> toDTOList(List<Discount> entityList) {
        if (entityList == null)
            return List.of();

        return entityList.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    default List<Discount> toEntityList(List<DiscountDTO> dtoList) {
        if (dtoList == null)
            return List.of();

        return dtoList.stream()
                .map(this::toEntity)
                .toList();
    }

}
