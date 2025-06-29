package com.example.menubackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Corresponde ao 'nome' do UserRegisterDTO

    @Column(unique = true)
    private String username; // Assumindo que este campo existe e precisa ser único.

    @Column(unique = true)
    private String email;

    @JsonIgnore // Garante que a senha não seja serializada em respostas JSON
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id")) // CORRIGIDO AQUI!
    @Enumerated(EnumType.STRING) // Armazena o nome da enum como String no banco de dados
    private Set<Role> roles = new HashSet<>(); // Usa seu Enum Role

    // Construtor para registro, alinhado com o UserRegisterDTO (nome, email, password)
    // E adicionando username (que será o email do DTO para consistência)
    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}