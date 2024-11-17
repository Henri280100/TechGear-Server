package com.v01.techgear_server.serviceImpls;

import org.springframework.stereotype.Service;

import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserPhoneNo;
import com.v01.techgear_server.repo.UserPhoneNoRepository;
import com.v01.techgear_server.service.UserPhoneNoService;
import com.v01.techgear_server.utils.PhoneNumberValidator;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserPhoneNoServiceImpl implements UserPhoneNoService {

    // private PhoneNumberValidator phoneNumberValidator;

    private final UserPhoneNoRepository userPhoneNoRepository;


    @Override
    public UserPhoneNo saveUserPhoneNo(User user) {
        // Validate user and phone number existence
        if (user == null || user.getPhoneNumbers() == null) {
            throw new IllegalArgumentException("User or phone number information is missing.");
        }
        // Initialize phone number validator
        PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();
        UserPhoneNo phoneNo = user.getPhoneNumbers();
        String countryCode = phoneNo.getCountryCode();
        // Validate phone number
        validatePhoneNumber(phoneNo, countryCode, phoneNumberValidator);
        // Check if the phone number is new or needs to be updated
        if (phoneNo.getId() == null) {
            // Save new phone number
            return saveNewPhoneNumber(user, phoneNo);
        } else {
            // Update existing phone number
            return updateExistingPhoneNumber(user, phoneNo);
        }
    }

    private void validatePhoneNumber(UserPhoneNo phoneNo, String countryCode, PhoneNumberValidator validator) {
        if (!validator.isValidPhoneNumber(phoneNo.getPhoneNo(), countryCode)) {
            throw new IllegalArgumentException("Invalid phone number: " + phoneNo.getPhoneNo());
        }
        // Format the phone number if valid
        phoneNo.setPhoneNo(validator.formatPhoneNumber(phoneNo.getPhoneNo(), countryCode));

    }

    private UserPhoneNo saveNewPhoneNumber(User user, UserPhoneNo phoneNo) {
        UserPhoneNo savedPhoneNo = userPhoneNoRepository.save(phoneNo);
        user.setPhoneNumbers(savedPhoneNo);
        return savedPhoneNo;
    }

    private UserPhoneNo updateExistingPhoneNumber(User user, UserPhoneNo phoneNo) {
        UserPhoneNo existingPhoneNo = userPhoneNoRepository.findById(phoneNo.getId())
                .orElseThrow(() -> new RuntimeException("Phone number not found"));
        existingPhoneNo.setPhoneNo(phoneNo.getPhoneNo());
        UserPhoneNo savedPhoneNo = userPhoneNoRepository.save(existingPhoneNo);
        user.setPhoneNumbers(savedPhoneNo);
        return savedPhoneNo;

    }
}
