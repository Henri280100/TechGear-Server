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
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "accessToken", "refreshToken" })
})
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long tokenId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "accessToken", length = 1000, nullable = false)
    private String accessToken;

    @Column(name = "refreshToken", length = 1000, nullable = false)
    private String refreshToken;

    @Column(name="tokenType")
    @Enumerated(EnumType.STRING)
    private TokenTypes tokenType;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name="createdDate")
    private Instant createdDate;

    @Column(name="expiresDate")
    private Instant expiresDate;
}
