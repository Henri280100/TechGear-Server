package com.v01.techgear_server.mapping;

import org.mapstruct.*;

import java.util.*;

import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
		ProductMapper.class,
		ImageMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductSpecificationMapper extends BaseMapper<ProductSpecification, ProductSpecificationDTO> {

	@Override
	@Mapping(target = "productSpecValue", source = "specValue")
	@Mapping(target = "productSpecName", source = "specName")
	@Mapping(target = "productSpecImage", source = "specImage")
	@Mapping(target = "productSpecId", source = "specId", ignore = true)
	@Mapping(target = "productSpecIcon", source = "icon")
	@Mapping(target = "detailDTO", source = "productDetail", ignore = true)
	ProductSpecificationDTO toDTO(ProductSpecification productSpecification);

	@Override
	@Mapping(target = "specName", source = "productSpecName")
	@Mapping(target = "specValue", source = "productSpecValue")
	@Mapping(target = "specImage", source = "productSpecImage")
	@Mapping(target = "specId", source = "productSpecId", ignore = true)
	@Mapping(target = "productDetail", source = "detailDTO", ignore = true)
	@Mapping(target = "icon", source = "productSpecIcon", ignore = true)
	ProductSpecification toEntity(ProductSpecificationDTO productSpecificationDTO);

	@Override
	default List<ProductSpecificationDTO> toDTOList(List<ProductSpecification> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<ProductSpecification> toEntityList(List<ProductSpecificationDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}

}
