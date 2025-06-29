package com.example.menubackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data; // Gera getters e setters (getNome(), getEmail(), getPassword())

@Data
public class UserRegisterDTO {
    @NotBlank(message = "O nome não pode estar em branco")
    @Size(min = 3, max = 40, message = "O nome deve ter entre 3 e 40 caracteres")
    private String nome; // Campo 'nome'

    @NotBlank(message = "O email não pode estar em branco")
    @Email(message = "Por favor, forneça um email válido")
    @Size(max = 40, message = "O email não pode ter mais de 40 caracteres")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco")
    @Size(min = 6, max = 20, message = "A senha deve ter entre 6 e 20 caracteres")
    private String password;
}