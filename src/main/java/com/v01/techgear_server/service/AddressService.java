package com.v01.techgear_server.service;

import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;

public interface AddressService {
    UserAddress createUserNewAddress(User user);
    User updateUserAddress(Long userId, String addressDetails);
}
