package com.ecommerce.api.repository;

import com.ecommerce.api.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserIdAndTokenHash(Long userId, String tokenHash);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId")
    List<RefreshToken> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now")LocalDateTime now);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.userId = :id")
    int deleteTokensUsingUserId(@Param("id") Long id);
}
