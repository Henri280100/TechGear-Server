package com.v01.techgear_server.mapping;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mapstruct.*;

import com.v01.techgear_server.dto.UserAddressDTO;
import com.v01.techgear_server.model.UserAddress;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel = "spring", uses = {
		AccountDetailsMapper.class,
})
public interface UserAddressMapper extends BaseMapper<UserAddress, UserAddressDTO> {

	@Override
	@Mapping(target = "state", source = "stateProvince")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "userCountry", source = "country")
	@Mapping(target = "userAddressType", source = "addressType")
	@Mapping(target = "userAddressLineTwo", source = "addressLineTwo")
	@Mapping(target = "userAddressLineOne", source = "addressLineOne")
	@Mapping(target = "cityAddress", source = "city")
	@Mapping(target = "accountDetailsDTO", source = "accountDetails", ignore = true)
	UserAddressDTO toDTO(UserAddress userAddress);

	@Override
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "stateProvince", source = "state", ignore = true)
	@Mapping(target = "primaryAddress", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "addressLineTwo", source = "userAddressLineTwo")
	@Mapping(target = "addressLineOne", source = "userAddressLineOne")
	@Mapping(target = "addressId", ignore = true)
	@Mapping(target = "country", source = "userCountry")
	@Mapping(target = "city", source = "cityAddress")
	@Mapping(target = "addressType", source = "userAddressType")
	@Mapping(target = "accountDetails", source = "accountDetailsDTO", ignore = true)
	UserAddress toEntity(UserAddressDTO userAddressDTO);

	@Override
	default List<UserAddressDTO> toDTOList(List<UserAddress> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<UserAddress> toEntityList(List<UserAddressDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}

	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "stateProvince", source = "state", ignore = true)
	@Mapping(target = "primaryAddress", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "addressLineTwo", source = "userAddressLineTwo")
	@Mapping(target = "addressLineOne", source = "userAddressLineOne")
	@Mapping(target = "addressId", ignore = true)
	@Mapping(target = "country", source = "userCountry")
	@Mapping(target = "city", source = "cityAddress")
	@Mapping(target = "addressType", source = "userAddressType")
	@Mapping(target = "accountDetails", source = "accountDetailsDTO", ignore = true)
	void updateEntityFromDTO(UserAddressDTO userAddressDTO,
	                         @MappingTarget
	                         UserAddress userAddress);
}
