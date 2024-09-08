package com.v01.techgear_server.serviceImpls;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.dto.UserPhoneNoDTO;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserPhoneNo;
import com.v01.techgear_server.repo.UserPhoneNoRepository;
import com.v01.techgear_server.repo.UserRepository;
import com.v01.techgear_server.service.UserPhoneNoService;
import com.v01.techgear_server.utils.PhoneNumberValidator;

@Service
public class UserPhoneNoServiceImpl implements UserPhoneNoService {

    // private PhoneNumberValidator phoneNumberValidator;

    @Autowired
    private UserPhoneNoRepository userPhoneNoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserPhoneNo saveUserPhoneNoDTO(UserDTO userDto, UserPhoneNoDTO phoneNoDTOs) {
        // Initialize phone number validator
        PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();

        // Check if the phone number already exists in the database
        Optional<UserPhoneNo> existingPhoneNo = userPhoneNoRepository.findByPhoneNo(
                phoneNoDTOs.getPhoneNo());

        if (existingPhoneNo.isPresent()) {
            return existingPhoneNo.get(); // Return existing phone number if it exists
        }

        // Validate and format phone number
        String countryCode = phoneNoDTOs.getCountryCode();
        if (phoneNumberValidator.isValidPhoneNumber(phoneNoDTOs.getPhoneNo(), countryCode)) {
            phoneNoDTOs.setPhoneNo(phoneNumberValidator.formatPhoneNumber(phoneNoDTOs.getPhoneNo(), countryCode));
        } else {
            throw new IllegalArgumentException("Invalid phone number: " + phoneNoDTOs.getPhoneNo());
        }

        // Convert DTO to entity
        UserPhoneNo newPhoneNo = modelMapper.map(phoneNoDTOs, UserPhoneNo.class);

        // Fetch the already saved User entity (ensure the User has been saved before
        // this step)
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userDto.getId()));

        // Set the user reference in UserPhoneNo
        newPhoneNo.setUsers(user);

        // Save the new phone number to the database
        return userPhoneNoRepository.save(newPhoneNo);

    }
}
