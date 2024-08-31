package com.v01.techgear_server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.UserPhoneNo;

@Repository
public interface UserPhoneNoRepository extends JpaRepository<UserPhoneNo, Long> {

}