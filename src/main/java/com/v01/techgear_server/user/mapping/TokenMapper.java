package com.v01.techgear_server.user.mapping;

import java.util.*;

import com.v01.techgear_server.user.dto.TokenDTO;
import com.v01.techgear_server.user.model.Token;
import com.v01.techgear_server.utils.*;

import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TokenMapper extends BaseMapper<Token, TokenDTO> {
	@Override
	@Mapping(target = "id", source = "tokenId", ignore = true)
	@Mapping(target = "clientId", source = "userId", ignore = true)
	TokenDTO toDTO(Token entity);

	@Override
	@Mapping(target = "userId", source = "clientId", ignore = true)
	@Mapping(target = "tokenId", source = "id", ignore = true)
	Token toEntity(TokenDTO dto);

	@Override
	default List<TokenDTO> toDTOList(List<Token> entityList) {
		return entityList == null ? Collections.emptyList() : entityList.stream()
		                                                                .map(this::toDTO)
		                                                                .toList();
	}

	@Override
	default List<Token> toEntityList(List<TokenDTO> dtoList) {
		return dtoList == null ? Collections.emptyList() : dtoList.stream()
		                                                          .map(this::toEntity)
		                                                          .toList();
	}
}