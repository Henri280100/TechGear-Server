package com.v01.techgear_server.serviceImpls;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.dto.UserAddressDTO;
import com.v01.techgear_server.dto.UserDTO;
import com.v01.techgear_server.model.MapBoxFeature;
import com.v01.techgear_server.model.MapBoxResponse;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;
import com.v01.techgear_server.repo.UserAddressRepository;
import com.v01.techgear_server.service.AddressService;
import com.v01.techgear_server.service.MapBoxService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private UserAddressRepository addressRepository;
    
    @Autowired
    private MapBoxService mapBoxService;
    
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public UserAddress handleUserAddress(UserDTO userDTO, User user) {
        if (userDTO.getAddresses() == null) {
            return null; // No address provided
        }

        UserAddressDTO addressDTO = userDTO.getAddresses();

        Optional<UserAddress> existingAddress = addressRepository.findByAddressDetails(addressDTO.getAddressDetails());

        if (existingAddress.isPresent()) {
            UserAddress address = existingAddress.get();
            user.setAddresses(address);
            return address;
        }

        UserAddress newAddress = modelMapper.map(addressDTO, UserAddress.class);

        if ((addressDTO.getLatitude() == null || addressDTO.getLongitude() == null) && 
            addressDTO.getAddressDetails() != null && !addressDTO.getAddressDetails().isEmpty()) {
            try {
                MapBoxResponse mapBoxResponse = mapBoxService.geocodeFeature(addressDTO.getAddressDetails());
                if (mapBoxResponse != null && mapBoxResponse.getFeatures() != null && !mapBoxResponse.getFeatures().isEmpty()) {
                    MapBoxFeature geocodedFeature = mapBoxResponse.getFeatures().get(0);
                    if (geocodedFeature.getCoordinates() != null && geocodedFeature.getCoordinates().length == 2) {
                        newAddress.setLatitude(geocodedFeature.getCoordinates()[1]); // latitude
                        newAddress.setLongitude(geocodedFeature.getCoordinates()[0]); // longitude
                    }
                }
            } catch (RuntimeException e) {
                // Handle exception, e.g., log error and/or rethrow
                throw new RuntimeException("Failed to geocode address: " + addressDTO.getAddressDetails(), e);
            }
        }

        UserAddress savedAddress = addressRepository.save(newAddress);
        user.setAddresses(savedAddress);

        return savedAddress;
    }

}
