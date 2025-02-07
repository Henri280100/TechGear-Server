package com.v01.techgear_server.mapping;

import java.util.*;

import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.dto.*;

@Mapper(componentModel = "spring", uses = {
		AccountDetailsMapper.class
})
public interface UserPhoneNoMapper extends BaseMapper<UserPhoneNo, UserPhoneNoDTO> {

	@Override
	@Mapping(target = "accountDetailsId", source = "accountDetails.accountDetailsId", ignore = true)
	UserPhoneNoDTO toDTO(UserPhoneNo entity);

	@Override
	@Mapping(target = "verifiedAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "phoneNumberVerified", ignore = true)
	@Mapping(target = "marketingConsentAt", ignore = true)
	@Mapping(target = "marketingConsent", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "accountDetails", ignore = true)
	UserPhoneNo toEntity(UserPhoneNoDTO dto);

	@Override
	default List<UserPhoneNoDTO> toDTOList(List<UserPhoneNo> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
	}

	@Override
	default List<UserPhoneNo> toEntityList(List<UserPhoneNoDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
	}
}
