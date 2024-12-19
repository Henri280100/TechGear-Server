package com.v01.techgear_server.mapping.mapper;

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
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    AccountDetailsDTO toDTO(AccountDetails entity);

    @Override
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "invoices", ignore = true)
    AccountDetails toEntity(AccountDetailsDTO dto);

}
