package com.example.menubackend.service;

import com.example.menubackend.model.User;
import com.example.menubackend.model.Role; // Seu Enum Role
import com.example.menubackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Exemplo de como você manipularia as roles
    public User updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        // Cria um novo Set para definir a(s) role(s) do usuário.
        // Se a intenção for adicionar uma nova role sem remover as existentes,
        // primeiro recupere o set atual e adicione a nova role.
        Set<Role> roles = new HashSet<>();
        roles.add(newRole); // Adiciona a nova role.

        user.setRoles(roles); // Define as roles do usuário
        return userRepository.save(user);
    }

    // Exemplo de como você verificaria se o usuário tem uma determinada role
    public boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return user.getRoles().contains(Role.ADMIN); // Verifica se o Set de roles contém a role ADMIN
    }

    // Se você tiver outros métodos que usavam user.getRole() ou user.setRole(),
    // adapte-os para usar user.getRoles() e user.setRoles() trabalhando com o Set.

    // Exemplo: Buscar usuário por email, se precisar
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
    }

    // ... outros métodos do seu UserService ...
}