package com.v01.techgear_server.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.v01.techgear_server.enums.AddressTypes;
import com.v01.techgear_server.user.model.UserAddress;
import com.v01.techgear_server.user.repository.UserAddressRepository;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class

AddressInitializer implements CommandLineRunner {
    private final UserAddressRepository userAddressRepository;
    @Override
    public void run(String... args) throws Exception {
        if (userAddressRepository.count() == 0) {
            userAddressRepository.saveAll(
                    Arrays.stream(AddressTypes.values())
                            .map(addressType -> {
                                UserAddress address = new UserAddress();
                                address.setAddressType(addressType);
                                return address;
                            })
                            .collect(Collectors.toList())
            );
        }
    }

}
