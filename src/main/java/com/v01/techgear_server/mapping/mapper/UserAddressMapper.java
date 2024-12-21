package com.v01.techgear_server.mapping.mapper;

import java.util.List;
import org.mapstruct.*;

import com.v01.techgear_server.dto.UserAddressDTO;
import com.v01.techgear_server.model.UserAddress;
import com.v01.techgear_server.utils.BaseMapper;

@Mapper(componentModel="spring", uses={
    AccountDetailsMapper.class,
}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserAddressMapper extends BaseMapper<UserAddress, UserAddressDTO> {
    
    @Override
    @Mapping(target = "addressId", source = "addressId")
    @Mapping(target = "addressLineOne", source = "addressLineOne")
    @Mapping(target = "addressLineTwo", source = "addressLineTwo")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "stateProvince", source = "stateProvince")
    @Mapping(target = "zipPostalCode", source = "zipPostalCode")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "primaryAddress", source = "primaryAddress")
    @Mapping(target = "accountDetails", qualifiedByName = "toAccountDetailsDTO")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserAddressDTO toDTO(UserAddress userAddress);

    @Override
    @Mapping(target = "addressId", source = "addressId")
    @Mapping(target = "addressLineOne", source = "addressLineOne")
    @Mapping(target = "addressLineTwo", source = "addressLineTwo")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "stateProvince", source = "stateProvince")
    @Mapping(target = "zipPostalCode", source = "zipPostalCode")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "primaryAddress", source = "primaryAddress")
    @Mapping(target = "accountDetails", qualifiedByName = "toAccountDetails")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserAddress toEntity(UserAddressDTO userAddressDTO);

    // Bulk mapping methods
    List<UserAddressDTO> toDTOList(List<UserAddress> userAddressList);
    List<UserAddress> toEntityList(List<UserAddressDTO> userAddressDTOList);


}
