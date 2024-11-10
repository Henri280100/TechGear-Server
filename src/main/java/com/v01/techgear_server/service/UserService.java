package com.v01.techgear_server.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.v01.techgear_server.model.User;

public interface UserService {
    User updateUsername(Long userId, String username);
    User updateUserEmail(Long userId, String email);
    // UserDTO updateUserPhoneNo(Long userId, String phoneNo, String countryCode);
    // UserDTO updateUserAddress(Long userId, String country, String addressDetails);

    User getUserById(Long userId);
    // User handler (only admin)
    User deleteUserById(Long userId);
    User deleteUsername(Long userId, String username);
    List<User> getAllUsers(String sortBy, String direction);
    
    // END
    User userUploadAvatarHandler(User user, MultipartFile userAvatar);
    User userUpdateAvatarHandler(User user, MultipartFile userAvatar);
    

}