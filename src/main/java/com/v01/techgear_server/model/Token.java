package com.v01.techgear_server.model;

import java.time.Instant;

import com.v01.techgear_server.enums.TokenTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tokenId;

    @Column(name = "user_id")
    private Long user_id;

    @Enumerated(EnumType.STRING)
    private TokenTypes tokenTypes;

    @Column(name="accessToken", length = 2088)
    private String accessToken;
    @Column(name="refreshToken", length = 2088)
    private String refreshToken;
    private boolean revoked;

    private Instant createdAt;
    private Instant expiresAt;
}
