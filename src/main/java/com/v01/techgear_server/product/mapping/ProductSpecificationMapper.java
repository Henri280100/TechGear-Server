package com.v01.techgear_server.product.mapping;

import com.v01.techgear_server.common.mapping.ImageMapper;
import com.v01.techgear_server.product.model.ProductSpecification;
import com.v01.techgear_server.product.dto.ProductSpecificationDTO;
import org.mapstruct.*;

import java.util.*;

import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
		ProductMapper.class,
		ImageMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductSpecificationMapper extends BaseMapper<ProductSpecification, ProductSpecificationDTO> {

	@Override
	@Mapping(target = "productSpecGuarantee", source = "guarantee")
	@Mapping(target = "productSpecValue", source = "specValue")
	@Mapping(target = "productSpecName", source = "specName")
	@Mapping(target = "productSpecId", source = "specId")
    ProductSpecificationDTO toDTO(ProductSpecification productSpecification);

	@Override
	@Mapping(target = "guarantee", source = "productSpecGuarantee")
	@Mapping(target = "productDetail", ignore = true)
	@Mapping(target = "specName", source = "productSpecName")
	@Mapping(target = "specValue", source = "productSpecValue")
	@Mapping(target = "specId", source = "productSpecId")
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
