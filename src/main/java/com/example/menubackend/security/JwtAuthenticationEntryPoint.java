package com.example.menubackend.security;

import com.fasterxml.jackson.databind.ObjectMapper; // Importe ObjectMapper
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType; // Importe MediaType
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper; // Injetar ObjectMapper

    // Construtor para injetar ObjectMapper.
    // Certifique-se que o ObjectMapper está disponível como um Bean no seu contexto Spring.
    // Geralmente, ele já é fornecido automaticamente pelo Spring Boot se você usa Jackson.
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        // Define o status HTTP como 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Define o tipo de conteúdo como JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Cria um mapa para a resposta JSON
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage()); // Mensagem da exceção
        body.put("path", request.getServletPath()); // Caminho da requisição

        // Escreve o mapa como JSON no corpo da resposta
        // Usa o ObjectMapper para serializar o mapa em JSON
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}