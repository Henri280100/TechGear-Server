package com.v01.techgear_server.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiryDuration;
}