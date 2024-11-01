package com.v01.techgear_server.serviceImpls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.exception.UserNotFoundException;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;
import com.v01.techgear_server.repo.UserAddressRepository;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.AddressService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private UserAddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserAddress createUserNewAddress(User user) {

        UserAddress userAddress = user.getAddresses();
        if (userAddress == null) {
            return null; // No address provided
        }
        // Save as a new entity
        UserAddress savedAddress = addressRepository.save(userAddress);
        // Link the saved address with the user
        user.setAddresses(savedAddress);
        return savedAddress; 
    }

    @Override
    public User updateUserAddress(Long userId, String addressDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User  not found"));
        UserAddress userAddress = user.getAddresses();
        if (userAddress == null) {
            userAddress = new UserAddress();
            userAddress.setUser(user);
        }
        userAddress.setAddressDetails(addressDetails);
        user.setAddresses(userAddress);
        return user;
    }
}
