package com.example.music_system.repository;

import com.example.music_system.model.Token;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expiryTime < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

    Optional<Token> findByUserId(int userId);
}
