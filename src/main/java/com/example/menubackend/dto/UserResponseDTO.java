package com.example.menubackend.dto;

import com.example.menubackend.model.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String nome;
    private Role role;
}