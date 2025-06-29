package com.example.menubackend.repository;

import com.example.menubackend.model.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
    // Encontra todos os itens de carrinho pertencentes a um carrinho específico.
    List<ItemCarrinho> findByCarrinhoId(Long carrinhoId);

    // Encontra um item específico em um carrinho para um determinado produto.
    Optional<ItemCarrinho> findByCarrinhoIdAndProdutoId(Long carrinhoId, Long produtoId);
}