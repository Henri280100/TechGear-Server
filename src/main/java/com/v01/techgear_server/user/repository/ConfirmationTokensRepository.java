package com.v01.techgear_server.user.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.user.model.ConfirmationTokens;

@Repository
public interface ConfirmationTokensRepository extends JpaRepository<ConfirmationTokens, Long> {
    // Optional<ConfirmationTokens> f(String token);
    Optional<ConfirmationTokens> findByConfirmToken(String confirmToken);
}
