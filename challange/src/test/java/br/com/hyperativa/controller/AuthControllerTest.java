package br.com.hyperativa.controller;

import br.com.hyperativa.controller.AuthController;
import br.com.hyperativa.exception.InvalidCredentialsException;
import br.com.hyperativa.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() {
        // Arrange
        String username = "admin";
        String password = "password";
        String fakeToken = "jwt-token";
        when(jwtService.generateToken(username)).thenReturn(fakeToken);

        Map<String, String> body = Map.of(
                "username", username,
                "password", password
        );

        // Act
        ResponseEntity<?> response = authController.login(body);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals(fakeToken, responseBody.get("access_token"));

        verify(jwtService, times(1)).generateToken(username);
    }

    @Test
    void login_withInvalidCredentials_shouldThrowInvalidCredentialsException() {
        Map<String, String> body = Map.of(
                "username", "wrong",
                "password", "wrongpass"
        );

        InvalidCredentialsException ex = assertThrows(
                InvalidCredentialsException.class,
                () -> authController.login(body)
        );

        assertEquals("Invalid username or password.", ex.getMessage());
        verify(jwtService, never()).generateToken(anyString());
    }
}

