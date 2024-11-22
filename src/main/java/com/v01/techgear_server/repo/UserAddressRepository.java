package com.v01.techgear_server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.enums.AddressTypes;
import com.v01.techgear_server.model.UserAddress;


@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    Optional<UserAddress> findByAddressDetails(String addressDetails);

    // Find all addresses for a specific user
    List<UserAddress> findByUserId(Long userId);

    // Find addresses by city
    List<UserAddress> findByCity(String city);

    // Find addresses by state/province
    List<UserAddress> findByStateProvince(String stateProvince);

    // Find primary addresses for a specific user
    List<UserAddress> findByUserIdAndPrimaryAddressTrue(Long userId);

    // Find addresses by address type
    List<UserAddress> findByAddressType(AddressTypes addressType);

}