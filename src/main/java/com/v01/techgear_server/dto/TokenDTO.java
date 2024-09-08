package com.v01.techgear_server.dto;

import lombok.Data;

@Data
public class TokenDTO {
    private Long tokensId;
    private String accessToken;
    private String refreshToken;
}
