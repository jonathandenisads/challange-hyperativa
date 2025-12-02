package br.com.hyperativa.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomAcessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "forbidden", accessDeniedException.getMessage());
    }

    private void sendResponse(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        String body = String.format("{\"error\":\"%s\",\"message\":\"%s\"}", error, message);
        response.getWriter().write(body);
    }
}