package com.v01.techgear_server.mapping;

import java.util.*;
import org.mapstruct.*;

import com.v01.techgear_server.model.WishlistItems;
import com.v01.techgear_server.utils.BaseMapper;
import com.v01.techgear_server.dto.WishlistItemsDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WishlistItemsMapper extends BaseMapper<WishlistItems, WishlistItemsDTO>{
    @Override
    WishlistItemsDTO toDTO(WishlistItems entity);

    @Override
    WishlistItems toEntity(WishlistItemsDTO dto);

    @Override
    default List<WishlistItemsDTO> toDTOList(List<WishlistItems> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<WishlistItems> toEntityList(List<WishlistItemsDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }
}
