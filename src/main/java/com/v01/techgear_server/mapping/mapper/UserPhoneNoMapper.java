package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.dto.*;

@Mapper(componentModel="spring", uses={
    AccountDetailsMapper.class
}, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserPhoneNoMapper extends BaseMapper<UserPhoneNo, UserPhoneNoDTO>{
    
    @Override
    @Mapping(target = "id", source = "id", ignore=true)
    @Mapping(target = "phoneNo", source = "phoneNo")
    @Mapping(target = "countryCode", source = "countryCode")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "purpose", source = "purpose")
    @Mapping(target = "phoneNumberVerified", source = "phoneNumberVerified", ignore=true)
    @Mapping(target = "primary", source = "primary")
    @Mapping(target = "accountDetails", source = "accountDetails", qualifiedByName = "toAccountDetailsDTO")
    @Mapping(target = "accountDetails.accountDetailsId", ignore=true)
    UserPhoneNoDTO toDTO(UserPhoneNo entity);

    @Override
    @Mapping(target = "id", source = "id")
    @Mapping(target = "phoneNo", source = "phoneNo")
    @Mapping(target = "countryCode", source = "countryCode")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "purpose", source = "purpose")
    @Mapping(target = "phoneNumberVerified", source = "phoneNumberVerified")
    @Mapping(target = "primary", source = "primary")
    @Mapping(target = "accountDetails", source = "accountDetails", qualifiedByName = "toAccountDetails")
    UserPhoneNo toEntity(UserPhoneNoDTO dto);

    @Override
    default List<UserPhoneNoDTO> toDTOList(List<UserPhoneNo> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }
    
    @Override
    default List<UserPhoneNo> toEntityList(List<UserPhoneNoDTO> dtoList) {
        return dtoList == null? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }
}
