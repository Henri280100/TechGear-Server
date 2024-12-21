package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import org.mapstruct.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.dto.*;
import com.v01.techgear_server.utils.*;

@Mapper(componentModel = "spring", uses = {
        AccountDetailsMapper.class
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper extends BaseMapper<User, UserDTO> {
    @Override
    @Mapping(target = "userId", source = "userId", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "accountDetails", source = "accountDetails", qualifiedByName = "toAccountDetailsDTO")
    @Mapping(target = "accountDetails.accountDetailsId", ignore = true)
    UserDTO toDTO(User entity);

    @Override
    @Mapping(target = "userId", source = "userId", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "accountDetails", source = "accountDetails", qualifiedByName = "toAccountDetails")
    @Mapping(target = "accountDetails.accountDetailsId", ignore = true)
    User toEntity(UserDTO dto);

    @Override
    default List<UserDTO> toDTOList(List<User> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<User> toEntityList(List<UserDTO> dtoList) {
        return dtoList == null ? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }
}
