package com.v01.techgear_server.mapping;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.v01.techgear_server.dto.WishlistDTO;
import com.v01.techgear_server.model.Wishlist;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", uses = {
        WishlistItemsMapper.class,
        ImageMapper.class
})
public interface WishlistMapper extends BaseMapper<Wishlist, WishlistDTO> {
    // Override and customize base methods with specific mappings
    @Override
    @Mapping(target = "image", ignore = true)
    WishlistDTO toDTO(Wishlist wishlist);

    @Override
    @Mapping(target = "wishlistImage", ignore = true)
    @Mapping(target = "accountDetails", ignore = true)
    Wishlist toEntity(WishlistDTO wishlistDTO);

    // Default implementation for bulk mapping methods
    @Override
    default List<WishlistDTO> toDTOList(List<Wishlist> entityList) {
        if (entityList == null) {
            return Collections.emptyList();
        }
        return entityList.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    default List<Wishlist> toEntityList(List<WishlistDTO> dtoList) {
        if (dtoList == null) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .map(this::toEntity)
                .toList();
    }

}
