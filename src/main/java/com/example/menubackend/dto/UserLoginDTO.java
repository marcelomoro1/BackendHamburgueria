package com.example.menubackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Gera getters e setters (getEmail(), getPassword())
public class UserLoginDTO {
    @NotBlank(message = "O email não pode estar em branco")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco")
    private String password;
}