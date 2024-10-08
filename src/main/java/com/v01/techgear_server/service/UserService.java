package com.v01.techgear_server.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.model.User;

public interface UserService {
    User updateUsername(Long userId, String username);
    User updateUserEmail(Long userId, String email);
    User updateUserPhoneNo(Long userId, String phoneNo, String countryCode);
    User updateUserAddress(Long userId, String country, String addressDetails);

    User getUserById(Long userId);
    // User handler (only admin)
    User deleteUserById(Long userId, User currentUser);
    User deleteUsername(Long userId, String username, User currentUser);
    List<User> getAllUsersSorted(String sortBy, String direction, User currentUser);
    
    // END
    User userUploadAvatarHandler(Long userId, MultipartFile userAvatar);
    User userUpdateAvatarHandler(Long userId, MultipartFile userAvatar);
    

}