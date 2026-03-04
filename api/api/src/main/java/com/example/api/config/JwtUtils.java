package com.example.api.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.*;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;                 // chave secreta (deve ser Base64)

    @Value("${jwt.expiration-ms}")
    private Long jwtExpirationMs;

    /*** Gera um token a partir do nome de usuário e papéis ***/
    public String generateToken(String username, Collection<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        // Converte a string da chave para um objeto Key
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)          // inclui papéis dentro do claim
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /*** Retorna o nome de usuário (subject) contido no token ***/
    public String getUsernameFromJwt(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    /*** Converte o token em Claims e devolve os papéis ***/
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwt(String token) {
        Claims claims = parseClaims(token).getBody();
        return (List<String>) claims.get("roles", List.class);
    }

    /*** Valida assinatura e data de validade ***/
    public boolean validateJwt(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expirado: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT inválido: {}", e.getMessage());
        }
        return false;
    }

    /*** Helper – parseia o token usando a mesma chave ***/
    private Jws<Claims> parseClaims(String token) {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}