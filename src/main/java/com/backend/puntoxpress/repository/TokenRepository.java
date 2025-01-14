package com.backend.puntoxpress.repository;

import com.backend.puntoxpress.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findAllNotExpiredNorRevokedByUserId(Long id);

    Token findByToken(String jwtToken);
}
