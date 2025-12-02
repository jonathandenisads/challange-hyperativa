package br.com.hyperativa.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "01234567890123456789012345678901"); // 32 chars
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1h
        jwtService.init();
    }

    @Test
    void generateToken_shouldReturnToken() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // token JWT possui 3 partes
    }

    @Test
    void getClaims_shouldReturnCorrectUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        Claims claims = jwtService.getClaims(token);
        assertEquals(username, claims.get("sub", String.class));
    }

    @Test
    void getUsername_shouldReturnCorrectUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        String extracted = jwtService.getUsername(token);
        assertEquals(username, extracted);
    }
}