package br.com.hyperativa.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Gerar Token
    public String generateToken(String username) {

        long now = System.currentTimeMillis();
        long exp = now + expiration;

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("iat", now / 1000);
        claims.put("exp", exp / 1000);

        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    // Validar + extrair claims
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return getClaims(token).get("sub", String.class);
    }
}
