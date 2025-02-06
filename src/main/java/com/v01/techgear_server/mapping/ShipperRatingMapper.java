package com.v01.techgear_server.mapping;

import java.util.*;

import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.dto.*;

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
