package com.v01.techgear_server.shipping.mapping;

import java.util.*;

import com.v01.techgear_server.shipping.model.ShipperRating;
import com.v01.techgear_server.order.mapping.OrderMapper;
import com.v01.techgear_server.shipping.dto.ShipperRatingDTO;
import org.mapstruct.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
		ShipperMapper.class,
		OrderMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ShipperRatingMapper extends BaseMapper<ShipperRating, ShipperRatingDTO> {

	@Override
	@Mapping(target = "userRating", source = "rating")
	@Mapping(target = "userComments", source = "comments")
	@Mapping(target = "userAccountDetails", source = "accountDetail", ignore = true)
	@Mapping(target = "shipperRatingId", source = "id", ignore = true)
	@Mapping(target = "shipperDTO", source = "shipper", ignore = true)
	@Mapping(target = "ratingDateTime", source = "ratingDate")
	ShipperRatingDTO toDTO(ShipperRating shipperRating);

	@Override
	@Mapping(target = "shipper", source = "shipperDTO")
	@Mapping(target = "ratingDate", source = "ratingDateTime")
	@Mapping(target = "rating", source = "userRating")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "comments", source = "userComments")
	@Mapping(target = "accountDetail", source = "userAccountDetails", ignore = true)
	ShipperRating toEntity(ShipperRatingDTO shipperRatingDTO);

	@Override
	default List<ShipperRatingDTO> toDTOList(List<ShipperRating> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<ShipperRating> toEntityList(List<ShipperRatingDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}
}
