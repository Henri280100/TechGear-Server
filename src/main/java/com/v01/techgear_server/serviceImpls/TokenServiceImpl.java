package com.v01.techgear_server.serviceImpls;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.v01.techgear_server.model.Token;
import com.v01.techgear_server.repo.TokenRepository;
import com.v01.techgear_server.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService{

    @Autowired
    private TokenRepository tokenRepository;

    public boolean isTokenValid(String id) {
        Optional<Token> tokens = tokenRepository.findById(id);
        if (tokens.isEmpty()) {
            return false; // No tokens found for the id
        }
        return tokens.stream().anyMatch(token -> !token.isRevoked());
    }

    @Override
    public void revokeToken(String id) {
        Optional<Token> tokens = tokenRepository.findById(id);
        if (tokens.isPresent()) {
            Token token = tokens.get();
            token.setRevoked(true);
            tokenRepository.save(token);
        } else {
            // Handle the case where no token was found
            throw new IllegalArgumentException("No tokens found for id: " + id);
        }
    }
    
}
