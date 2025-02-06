package com.v01.techgear_server.mapping;

import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.model.Role;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.utils.BaseMapper;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {AccountDetailsMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper extends BaseMapper<User, UserDTO> {
	@Override
	@Mapping(target = "userRoles", source = "roles", ignore = true)
	UserDTO toDTO(User entity);

	@Override
	@Mapping(target = "roles", source = "userRoles", ignore = true)
	@Mapping(target = "connections", ignore = true)
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "provider", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
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


	@Mapping(target = "roles", source = "userRoles", ignore = true)
	@Mapping(target = "connections", ignore = true)
	@Mapping(target = "updatedDate", ignore = true)
	@Mapping(target = "provider", ignore = true)
	@Mapping(target = "password", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "confirmationTokens", ignore = true)
	@Mapping(target = "authorities", ignore = true)
	@Mapping(target = "accountLocked", ignore = true)
	@Mapping(target = "accountExpired", ignore = true)
	void updateEntityFromDTO(UserDTO userDTO,
	                         @MappingTarget
	                         User user);

}
