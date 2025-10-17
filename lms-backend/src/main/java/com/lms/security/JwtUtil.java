package com.lms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private Key getSigningKey() {
        // Try decoding as standard Base64, then URL-safe Base64, otherwise fall back to raw UTF-8 bytes.
        if (SECRET_KEY == null || SECRET_KEY.isBlank()) {
            throw new IllegalStateException("jwt.secret is not configured or empty");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception ex1) {
            try {
                // Accept URL-safe Base64 (characters like '-' and '_')
                byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
                return Keys.hmacShaKeyFor(keyBytes);
            } catch (Exception ex2) {
                // Final fallback: use raw UTF-8 bytes of the secret string
                byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
                return Keys.hmacShaKeyFor(keyBytes);
            }
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            logger.debug("Token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.debug("JWT parsing error: {}", e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long validityMillis = 1000L * 60 * 60 * 10; // 10 hours
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validityMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            boolean valid = (extractedUsername.equals(username) && !isTokenExpired(token));
            if (!valid) logger.debug("Token validation failed for user {}", username);
            return valid;
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("Token validation error: {}", e.getMessage());
            return false;
        }
    }
}
