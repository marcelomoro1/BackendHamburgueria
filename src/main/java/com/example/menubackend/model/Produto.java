package com.example.menubackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;  
import lombok.Data;
import lombok.NoArgsConstructor;   
import lombok.AllArgsConstructor;  
import java.math.BigDecimal; 

@Data 
@NoArgsConstructor 
@AllArgsConstructor
@Entity 
public class Produto {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String nome;
    private String descricao;

    private BigDecimal preco; 

    @Enumerated(EnumType.STRING) 
    private Categoria categoria; 

    private Boolean disponibilidade;
    private String imagem;

}
