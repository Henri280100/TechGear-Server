package com.v01.techgear_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

import com.v01.techgear_server.enums.TokenTypes;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for Token information")
public class TokenDTO {
    @Schema(description = "Token ID", example = "1")
    private Long id;

    @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Client ID", example = "client_id_example")
    private Long clientId;

    @Schema(description = "Refresh token", example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    private TokenTypes tokenType;

    @Schema(description = "Revoked status", example = "false")
    private boolean revoked;

    @Schema(description = "Token expiration time in seconds", example = "604800")
    private Long expiresIn;
    
    @Schema(description = "Token creation date", example = "2022-01-20T14:30:00.000Z")
    private Instant createdDate;

    @Schema(description = "Token expiration date", example = "2022-01-20T15:30:00.000Z")
    private Instant expiresDate;
}
