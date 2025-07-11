package com.example.menubackend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemPedidoResponseDTO {
    private Long id; // ID do ItemPedido
    private Long produtoId; // ID do Produto
    private String nomeProduto; // Nome do produto para exibição
    private String imagemProduto; // Imagem do produto para exibição
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal; // PrecoUnitario * Quantidade
}