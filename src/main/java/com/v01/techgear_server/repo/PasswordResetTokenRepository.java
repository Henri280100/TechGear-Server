package com.v01.techgear_server.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.PasswordResetToken;
import java.util.List;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
    Optional<PasswordResetToken> findByToken(String token);
}