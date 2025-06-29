package com.example.menubackend.dto;

import com.example.menubackend.model.Categoria; // Importar a enum Categoria
import lombok.Data;

import java.math.BigDecimal; // Usar BigDecimal para o preço

@Data
public class ProdutoDTO {
    private Long id; // Pode ser nulo para criação, preenchido para atualização/resposta
    private String nome;
    private String descricao;
    private BigDecimal preco; // Tipo de dado correto para moeda
    private Categoria categoria; // Usar o Enum Categoria
    private Boolean disponibilidade; // Renomeado de 'status' para clareza
    private String imagem;
}