package com.v01.techgear_server.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Modifying
    @Query("SELECT t from Token t WHERE t.user_id = :user_id")
    List<Token> findAllByUser_Id(Long user_id);

    Optional<Token> findByRefreshToken(String refreshToken);
}
