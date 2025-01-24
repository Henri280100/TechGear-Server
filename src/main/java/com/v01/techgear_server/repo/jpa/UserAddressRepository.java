package com.v01.techgear_server.repo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.enums.AddressTypes;
import com.v01.techgear_server.model.UserAddress;


@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    
    // Find addresses by city
    List<UserAddress> findByCity(String city);

    // Find addresses by state/province
    List<UserAddress> findByStateProvince(String stateProvince);

    // Find addresses by address type
    List<UserAddress> findByAddressType(AddressTypes addressType);

    // Find addresses by country
    List<UserAddress> findByCountry(String country);

}