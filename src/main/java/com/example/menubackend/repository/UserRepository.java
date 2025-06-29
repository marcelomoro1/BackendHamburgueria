package com.example.menubackend.repository;

import com.example.menubackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Métodos necessários para as validações no AuthController
    Boolean existsByUsername(String username); // Para verificar se o username já existe
    Boolean existsByEmail(String email);       // Para verificar se o email já existe
}