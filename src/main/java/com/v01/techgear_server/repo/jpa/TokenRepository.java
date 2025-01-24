package com.v01.techgear_server.repo.jpa;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByRefreshToken(String refreshToken);

    @Query("SELECT t FROM Token t WHERE t.userId = :userId ORDER BY t.createdDate DESC")
    List<Token> findAllByUser_Id(@Param("userId") Long userId);

    @Query("""
            SELECT t FROM Token t
            WHERE t.refreshToken = :refreshToken
            AND t.revoked = false
            AND t.expiresDate > CURRENT_TIMESTAMP
            ORDER BY t.createdDate DESC
            LIMIT 1
            """)
    Optional<Token> findLatestValidRefreshToken(@Param("refreshToken") String refreshToken);

    @Query("SELECT t FROM Token t WHERE t.userId = :userId AND t.revoked = false AND t.expiresDate > CURRENT_TIMESTAMP")
    List<Token> findAllValidTokensByUser(@Param("userId") Long userId);

    @Query("SELECT t FROM Token t WHERE t.accessToken = :accessToken AND t.revoked = false ORDER BY t.createdDate DESC")
    Optional<Token> findLatestValidAccessToken(@Param("accessToken") String accessToken);

    @Query("SELECT t FROM Token t WHERE t.userId = :userId AND t.revoked = false ORDER BY t.createdDate DESC")
    List<Token> findAllValidTokensByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Token t SET t.revoked = true WHERE t.userId = :userId AND t.revoked = false")
    void revokeAllUserTokens(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresDate < :now OR t.revoked = true")
    void deleteExpiredAndRevokedTokens(@Param("now") Instant now);
}
