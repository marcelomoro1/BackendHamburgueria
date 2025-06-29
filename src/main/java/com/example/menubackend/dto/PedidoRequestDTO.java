package com.example.menubackend.dto;

import lombok.Data;

// Neste cenário, o PedidoRequestDTO pode ser vazio ou ter um campo para observações, se necessário.
// A lógica de criação do pedido virá do carrinho associado ao usuário logado.
@Data
public class PedidoRequestDTO {
    // String observacoes; // Exemplo de campo opcional para o cliente adicionar
}