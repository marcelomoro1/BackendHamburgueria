package com.example.menubackend.repository;

import com.example.menubackend.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByDisponibilidadeTrue();
}