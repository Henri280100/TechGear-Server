package com.v01.techgear_server.mapping.mapper;

import java.util.*;
import com.v01.techgear_server.model.*;
import com.v01.techgear_server.utils.*;
import com.v01.techgear_server.dto.*;

import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TokenMapper extends BaseMapper<Token, TokenDTO> {
    @Override
    @Mapping(target="accessToken", source="accessToken")
    @Mapping(target="refreshToken", source="refreshToken")
    @Mapping(target="tokenType", source="tokenType")
    @Mapping(target="expiresIn", source="expiresIn")
    TokenDTO toDTO(Token entity);

    @Override
    @Mapping(target="accessToken", source="accessToken")
    @Mapping(target="refreshToken", source="refreshToken")
    @Mapping(target="tokenType", source="tokenType")
    @Mapping(target="expiresIn", source="expiresIn")
    Token toEntity(TokenDTO dto);

    @Override
    default List<TokenDTO> toDTOList(List<Token> entityList) {
        return entityList == null ? Collections.emptyList() : entityList.stream().map(this::toDTO).toList();
    }

    @Override
    default List<Token> toEntityList(List<TokenDTO> dtoList) {
        return dtoList == null? Collections.emptyList() : dtoList.stream().map(this::toEntity).toList();
    }
}