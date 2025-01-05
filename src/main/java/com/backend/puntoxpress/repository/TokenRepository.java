package com.backend.puntoxpress.repository;

import com.backend.puntoxpress.Dto.RegisterDTO;
import com.backend.puntoxpress.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {

}
