package com.example.menubackend.repository;

import com.example.menubackend.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    // Encontra um carrinho associado a um ID de usuário específico.
    // Como um usuário deve ter apenas um carrinho ativo, retorna um Optional.
    Optional<Carrinho> findByUsuarioId(Long userId);
}