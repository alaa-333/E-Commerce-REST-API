package com.e_commerce.E_Commerce.REST.API.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;


    // generate secret key
    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // =========== token generation ========

    public String generateToken(UserDetails userDetails)
    {
        Map<String , Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return doGenerateToken(claims, userDetails.getUsername());
    }

    public String generateToken(String username, Map<String, Object> claims)
    {
        return doGenerateToken(claims, username);
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // ========= token validation =========

    public boolean validateToken(String token) // Checks the signature using the Secret Key issued by your server
    {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return  true;
        } catch (JwtException e)
        {
            return false;
        }
    }

    public boolean validationToken(String token , UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token)
    {
        Date expireDate =   extractClaim(token,Claims::getExpiration);
        return expireDate.before(new Date());
    }


    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsTFunction)
    {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }




}
