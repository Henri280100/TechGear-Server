package com.v01.techgear_server.repo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.v01.techgear_server.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByTokensId(String tokensId);
}
