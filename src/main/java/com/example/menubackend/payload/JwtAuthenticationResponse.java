package com.example.menubackend.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer"; // Padrão JWT

    // Construtor adicional para aceitar apenas o token de acesso (para o AuthController)
    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}