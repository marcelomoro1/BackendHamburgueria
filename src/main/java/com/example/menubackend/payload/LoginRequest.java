package com.example.menubackend.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data; // Importe a anotação @Data do Lombok

@Data // Gera automaticamente getters, setters, toString, equals e hashCode
public class LoginRequest {
    @NotBlank(message = "O nome de usuário ou email não pode estar em branco")
    private String usernameOrEmail;

    @NotBlank(message = "A senha não pode estar em branco")
    private String password;
}