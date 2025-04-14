package com.v01.techgear_server.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.user.model.PasswordResetToken;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
    Optional<PasswordResetToken> findByToken(String token);
}
