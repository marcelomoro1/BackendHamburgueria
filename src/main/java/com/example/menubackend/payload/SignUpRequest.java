package com.example.menubackend.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data; // Importe a anotação @Data do Lombok

@Data // Gera automaticamente getters, setters, toString, equals e hashCode
public class SignUpRequest {
    @NotBlank(message = "O nome não pode estar em branco")
    @Size(min = 3, max = 40, message = "O nome deve ter entre 3 e 40 caracteres")
    private String name;

    @NotBlank(message = "O nome de usuário não pode estar em branco")
    @Size(min = 3, max = 15, message = "O nome de usuário deve ter entre 3 e 15 caracteres")
    private String username;

    @NotBlank(message = "O email não pode estar em branco")
    @Email(message = "Por favor, forneça um email válido")
    @Size(max = 40, message = "O email não pode ter mais de 40 caracteres")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco")
    @Size(min = 6, max = 20, message = "A senha deve ter entre 6 e 20 caracteres")
    private String password;
}