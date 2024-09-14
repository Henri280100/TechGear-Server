package com.v01.techgear_server.service;

import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;

public interface AddressService {
    UserAddress handleUserAddress(UserDTO userDTO, User user);    
}
