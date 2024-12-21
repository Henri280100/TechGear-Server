package com.v01.techgear_server.mapping.mapper;

import java.util.List;
import org.mapstruct.*;
import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {
        UserAddressMapper.class,
        UserPhoneNoMapper.class
}, nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface AccountDetailsMapper extends BaseMapper<AccountDetails, AccountDetailsDTO> {
    
    @Override
    @Mapping(target = "accountDetailsId", source = "accountDetailsId")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "userType", source = "userType")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "userPhoneNo", qualifiedByName = "toUserPhoneNoDTO")
    @Mapping(target = "userAddress", qualifiedByName = "toUserAddressDTO")
    AccountDetailsDTO toDTO(AccountDetails accountDetails);

    @Override
    @Mapping(target = "accountDetailsId", source = "accountDetailsId")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "userType", source = "userType")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "userPhoneNo", qualifiedByName = "toUserPhoneNo")
    @Mapping(target = "userAddress", qualifiedByName = "toUserAddress")
    AccountDetails toEntity(AccountDetailsDTO accountDetailsDTO);

    // Bulk mapping methods
    List<AccountDetailsDTO> toDTOList(List<AccountDetails> accountDetailsList);
    List<AccountDetails> toEntityList(List<AccountDetailsDTO> accountDetailsDTOList);


}
