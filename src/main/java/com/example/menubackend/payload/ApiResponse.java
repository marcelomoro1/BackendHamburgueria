package com.example.menubackend.payload;

import lombok.AllArgsConstructor; // Importe para construtor com todos os argumentos
import lombok.Data; // Ou @Getter para apenas getters, ou @Value para imutabilidade

@Data // Ou @Value para imutabilidade com final fields e construtor all-args
@AllArgsConstructor // Gera um construtor com todos os campos (success, message)
public class ApiResponse {
    private Boolean success;
    private String message;
}