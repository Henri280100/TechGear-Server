package com.v01.techgear_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}