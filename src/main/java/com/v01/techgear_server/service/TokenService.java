package com.v01.techgear_server.service;

public interface TokenService {
    boolean isTokenValid(String token);
    void revokeToken(String token);
}
