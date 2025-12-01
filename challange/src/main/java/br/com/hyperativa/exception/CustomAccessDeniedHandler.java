package br.com.hyperativa.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//@Component
public class CustomAccessDeniedHandler  {
//
//    @Override
//    public void handle(HttpServletRequest request,
//                       HttpServletResponse response,
//                       AccessDeniedException accessDeniedException)
//            throws IOException, ServletException {
//
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setContentType("application/json");
//
//        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("timestamp", LocalDateTime.now().toString());
//        responseBody.put("status", 403);
//        responseBody.put("error", "Access Denied");
//        responseBody.put("message", "You don't have permission to access this resource");
//        responseBody.put("path", request.getRequestURI());
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.writeValue(response.getOutputStream(), responseBody);
//    }

}
