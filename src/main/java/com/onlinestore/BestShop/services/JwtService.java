package com.onlinestore.BestShop.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.refreshTokenExpiration}")
    private int refreshTokenExpiration;

    @Value("${spring.jwt.accessTokenExpiration}")
    private int accessTokenExpiration;

    public String generateRefreshToken(String email){
        return generate(email, refreshTokenExpiration);
    }

    public String generateAccessToken(String email){
        return generate(email, accessTokenExpiration);
    }

    private String generate(String email, long tokenExpiration) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }


    public boolean validateToken(String token){
        try {
            return getClaimsFromToken(token).getExpiration().after(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailFromToken(String token){
        return getClaimsFromToken(token).getSubject();
    }
}
