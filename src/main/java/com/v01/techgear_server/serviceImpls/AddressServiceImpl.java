package com.v01.techgear_server.serviceImpls;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.v01.techgear_server.dto.UserAddressDTO;
import com.v01.techgear_server.enums.AddressTypes;
import com.v01.techgear_server.model.User;
import com.v01.techgear_server.model.UserAddress;
import com.v01.techgear_server.repo.UserAddressRepository;
import com.v01.techgear_server.service.AddressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final UserAddressRepository addressRepository;

    @Override
    public UserAddress createUserNewAddress(User user) {

        UserAddress userAddress = user.getAddresses();
        if (userAddress == null) {
            return null; // No address provided
        }
        userAddress.setCreatedAt(LocalDateTime.now());
        userAddress.setUpdatedAt(LocalDateTime.now());
        // Save as a new entity
        UserAddress savedAddress = addressRepository.save(userAddress);
        // Link the saved address with the user
        user.setAddresses(savedAddress);
        return savedAddress;
    }

    @Override
    public CompletableFuture<UserAddressDTO> updateUserAddress(Long userAddressId, UserAddressDTO userAddressDTO) {
        return CompletableFuture.supplyAsync(() -> addressRepository.findById(userAddressId)
                .map(existingAddress -> {

                    if (!isValidAddressType(userAddressDTO.getType())) {
                        throw new RuntimeException("Invalid address type");
                    }

                    existingAddress.setAddressLineOne(userAddressDTO.getAddressLineOne());
                    existingAddress.setAddressLineTwo(userAddressDTO.getAddressLineTwo());
                    existingAddress.setCity(userAddressDTO.getCity());
                    existingAddress.setStateProvince(userAddressDTO.getStateProvince());
                    existingAddress.setZipPostalCode(userAddressDTO.getZipPostalCode());
                    existingAddress.setCountry(userAddressDTO.getCountry());
                    existingAddress.setAddressType(userAddressDTO.getType());
                    existingAddress.setPrimaryAddress(userAddressDTO.isPrimaryAddress());
                    existingAddress.setUpdatedAt(LocalDateTime.now());

                    if (userAddressDTO.getUser() != null) {
                        existingAddress.setUser(userAddressDTO.getUser().toEntity());
                    }

                    return UserAddressDTO.fromEntity(addressRepository.save(existingAddress));
                })
                .orElseThrow(() -> new RuntimeException("User address not found")));
    }

    /**
     * Validates if the given address type is one of the defined address types in
     * the {@link AddressTypes} enum.
     *
     * @param type The address type to validate.
     * @return {@code true} if the address type is valid, {@code false} otherwise.
     */
    private boolean isValidAddressType(AddressTypes type) {
        for (AddressTypes isValidTypes : AddressTypes.values()) {
            if (isValidTypes.name().equalsIgnoreCase(type.name())) {
                return true;
            }
        }
        return false;
    }

}
