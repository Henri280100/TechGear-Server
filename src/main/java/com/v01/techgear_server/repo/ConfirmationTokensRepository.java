package com.v01.techgear_server.repo;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.ConfirmationTokens;

@Repository
public interface ConfirmationTokensRepository extends JpaRepository<ConfirmationTokens, Long> {
    // Optional<ConfirmationTokens> f(String token);
    Optional<ConfirmationTokens> findByConfirmToken(String confirmToken);
}
