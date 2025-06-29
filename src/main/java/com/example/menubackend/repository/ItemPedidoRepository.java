package com.example.menubackend.repository;

import com.example.menubackend.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    // Encontra todos os itens pertencentes a um pedido específico.
    List<ItemPedido> findByPedidoId(Long pedidoId);
}