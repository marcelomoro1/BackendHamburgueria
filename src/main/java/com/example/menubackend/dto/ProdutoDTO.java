package com.example.menubackend.dto;

import com.example.menubackend.model.Categoria; // Importar a enum Categoria
import lombok.Data;

import java.math.BigDecimal; // Usar BigDecimal para o preço

@Data
public class ProdutoDTO {
    private Long id; 
    private String nome;
    private String descricao;
    private BigDecimal preco; 
    private Categoria categoria; 
    private Boolean disponibilidade; 
    private String imagem;
}
