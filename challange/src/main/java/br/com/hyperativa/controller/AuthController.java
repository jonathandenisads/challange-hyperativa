package br.com.hyperativa.controller;

import br.com.hyperativa.exception.InvalidCredentialsException;
import br.com.hyperativa.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String user = body.get("username");
        String pass = body.get("password");

        if ("admin".equals(user) && "password".equals(pass)) {
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(Map.of("access_token", token));
        }

        throw new InvalidCredentialsException("Invalid username or password.");
    }

}
