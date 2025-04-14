package com.v01.techgear_server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.user.model.AccountDetails;

@Repository
public interface AccountDetailsRepository extends JpaRepository<AccountDetails, Long>{
	AccountDetails findAccountDetailsByAccountDetailsId(Long id);
    
}