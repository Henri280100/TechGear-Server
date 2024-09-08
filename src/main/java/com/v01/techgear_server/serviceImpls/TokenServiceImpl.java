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

    public boolean isTokenValid(String tokensId) {
        List<Token> tokens = tokenRepository.findByTokensId(tokensId);
        if (tokens.isEmpty()) {
            return false; // No tokens found for the id
        }
        return tokens.stream().anyMatch(token -> !token.isRevoked());
    }

    @Override
    public void revokeToken(String tokensId) {
        List<Token> tokens = tokenRepository.findByTokensId(tokensId);
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("No tokens found for id: " + tokensId);
        }
        for (Token token : tokens) {
            token.setRevoked(true);
            tokenRepository.save(token);
        }
    }
    
}
