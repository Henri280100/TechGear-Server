package com.v01.techgear_server.serviceImpls;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.dto.UserPhoneNoDTO;
import com.v01.techgear_server.model.UserPhoneNo;
import com.v01.techgear_server.repo.UserPhoneNoRepository;
import com.v01.techgear_server.service.UserPhoneNoService;
import com.v01.techgear_server.utils.PhoneNumberValidator;

@Service
public class UserPhoneNoServiceImpl implements UserPhoneNoService {

    private PhoneNumberValidator phoneNumberValidator;

    @Autowired
    private UserPhoneNoRepository userPhoneNoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void saveUserPhoneNoDTO(List<UserPhoneNoDTO> phoneNoDTOs) {
        for (UserPhoneNoDTO phoneNoDTO : phoneNoDTOs) {

            String countryCode = phoneNoDTO.getCountryCode();

            if (phoneNumberValidator.isValidPhoneNumber(phoneNoDTO.getPhoneNo(), countryCode)) {
                phoneNoDTO.setPhoneNo(
                        phoneNumberValidator.formatPhoneNumber(phoneNoDTO.getPhoneNo(), countryCode));
            }

            UserPhoneNo userPhoneNo = modelMapper.map(phoneNoDTO, UserPhoneNo.class);
            userPhoneNoRepository.save(userPhoneNo);
        }
    }

}
