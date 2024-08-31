package com.v01.techgear_server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.v01.techgear_server.model.Email;

public interface EmailRepository extends JpaRepository<Email, Long>{
    @Modifying
    @Query("SELECT e FROM Email e WHERE e.verificationToken = ?1")
    Email findByVerificationToken(String urlToken);

    @Modifying
    @Query("SELECT e FROM Email e WHERE e.emailAddress = ?1")
    Email findByEmailAddress(String emailAddress);
}
