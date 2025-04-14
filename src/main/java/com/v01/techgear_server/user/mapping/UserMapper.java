package com.v01.techgear_server.user.mapping;

import com.v01.techgear_server.user.dto.UserDTO;
import com.v01.techgear_server.user.model.User;
import com.v01.techgear_server.utils.BaseMapper;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountDetailsMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper extends BaseMapper<User, UserDTO> {
	@Override
	@Mapping(target = "userRoles", ignore = true)
	@Mapping(target = "isActive", source = "active")
	UserDTO toDTO(User entity);

	@Override
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "provider", ignore = true)
	@Mapping(target = "permissions", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "lastLoginActiveBefore", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "connections", ignore = true)
	@Mapping(target = "confirmationTokens", ignore = true)
	@Mapping(target = "authorities", ignore = true)
	@Mapping(target = "accountLocked", ignore = true)
	@Mapping(target = "accountExpired", ignore = true)
	User toEntity(UserDTO dto);


	@Override
	default List<UserDTO> toDTOList(List<User> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<User> toEntityList(List<UserDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}


	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "provider", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "connections", ignore = true)
	@Mapping(target = "confirmationTokens", ignore = true)
	@Mapping(target = "authorities", ignore = true)
	@Mapping(target = "accountLocked", ignore = true)
	@Mapping(target = "accountExpired", ignore = true)
	@Mapping(target = "permissions", ignore = true)
	@Mapping(target = "lastLoginActiveBefore", ignore = true)
	void updateEntityFromDTO(UserDTO userDTO,
							 @MappingTarget
	                         User user);

}
