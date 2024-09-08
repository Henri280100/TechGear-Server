package com.v01.techgear_server.service;

import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.dto.UserPhoneNoDTO;
import com.v01.techgear_server.model.UserPhoneNo;

import java.util.List;
public interface UserPhoneNoService {
    UserPhoneNo saveUserPhoneNoDTO(UserDTO userDto, UserPhoneNoDTO userPhoneNoDTO);
}
