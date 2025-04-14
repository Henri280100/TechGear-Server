package com.v01.techgear_server.user.mapping;

import java.util.Collections;
import java.util.List;

import com.v01.techgear_server.user.dto.AccountDetailsDTO;
import com.v01.techgear_server.user.model.AccountDetails;
import org.mapstruct.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
		UserAddressMapper.class,
		UserPhoneNoMapper.class
})
public interface AccountDetailsMapper extends BaseMapper<AccountDetails, AccountDetailsDTO> {

	@Override
	@Mapping(target = "userType", source = "userTypes")
	@Mapping(target = "userProfileImage", ignore = true)
	@Mapping(target = "userPhoneNoDTOS", source = "phoneNumbers", ignore = true)
	@Mapping(target = "userLastName", source = "lastName")
	@Mapping(target = "userGender", source = "genders")
	@Mapping(target = "userFirstName", source = "firstName")
	@Mapping(target = "userDateOfBirth", source = "dateOfBirth")
	@Mapping(target = "userAddressDTOS", source = "addresses", ignore = true)
	AccountDetailsDTO toDTO(AccountDetails accountDetails);


	@Override
	@Mapping(target = "wishlists", ignore = true)
	@Mapping(target = "users", ignore = true)
	@Mapping(target = "userTypes", source = "userType")
	@Mapping(target = "userAvatar", ignore = true)
	@Mapping(target = "totalReviews", ignore = true)
	@Mapping(target = "shipperRatings", ignore = true)
	@Mapping(target = "registrationDate", ignore = true)
	@Mapping(target = "productRatings", ignore = true)
	@Mapping(target = "phoneNumbers", source = "userPhoneNoDTOS", ignore = true)
	@Mapping(target = "phoneNumberVerified", ignore = true)
	@Mapping(target = "paymentMethods", ignore = true)
	@Mapping(target = "order", ignore = true)
	@Mapping(target = "lastUpdatedTimestamp", ignore = true)
	@Mapping(target = "lastName", source = "userLastName")
	@Mapping(target = "lastLoginTime", ignore = true)
	@Mapping(target = "invoices", ignore = true)
	@Mapping(target = "genders", source = "userGender")
	@Mapping(target = "firstName", source = "userFirstName")
	@Mapping(target = "emailVerified", ignore = true)
	@Mapping(target = "dateOfBirth", source = "userDateOfBirth")
	@Mapping(target = "addresses", source = "userAddressDTOS", ignore = true)
	@Mapping(target = "accountUpdatedTime", ignore = true)
	@Mapping(target = "accountAgeDays", ignore = true)
	AccountDetails toEntity(AccountDetailsDTO accountDetailsDTO);

	@Override
	default List<AccountDetailsDTO> toDTOList(List<AccountDetails> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<AccountDetails> toEntityList(List<AccountDetailsDTO> dtoList) {

		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}
}
