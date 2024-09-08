package com.v01.techgear_server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.UserPhoneNo;
import java.util.Optional;


@Repository
public interface UserPhoneNoRepository extends JpaRepository<UserPhoneNo, Long> {
    Optional<UserPhoneNo> findByPhoneNo(String phoneNo);
}
