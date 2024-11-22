package com.v01.techgear_server.service;

import java.util.concurrent.CompletableFuture;

import com.v01.techgear_server.dto.UserAddressDTO;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;

public interface AddressService {
    UserAddress createUserNewAddress(User user);
    CompletableFuture<UserAddressDTO> updateUserAddress(Long userAddressId, UserAddressDTO userAddressDTO);
}
