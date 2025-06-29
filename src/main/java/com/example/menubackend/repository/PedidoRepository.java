package com.example.menubackend.repository;

import com.example.menubackend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Encontra todos os pedidos feitos por um usuário específico, ordenados pela data do pedido
    // em ordem decrescente (do mais novo para o mais antigo).
    List<Pedido> findByUsuarioIdOrderByDataPedidoDesc(Long userId);
}