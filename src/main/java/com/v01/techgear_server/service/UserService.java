package com.v01.techgear_server.service;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.model.User;

public interface UserService {
    User updateUsername(Long userId, String username);
    User updateUserEmail(Long userId, String email);
    User updateUserPhoneNo(Long userId, String phoneNo, String countryCode);
    User updateUserAddress(Long userId, String country, String addressDetails);

    User getUserById(Long userId);
    User userUploadAvatarHandler(Long userId, MultipartFile userAvatar);
    User userUpdateAvatarHandler(Long userId, MultipartFile userAvatar);
    
}