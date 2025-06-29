package com.example.menubackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated; // Necessário para o Enum Categoria
import jakarta.persistence.EnumType;   // Necessário para o Enum Categoria
import lombok.Data;
import lombok.NoArgsConstructor;    // Adicione se estiver usando Lombok para construtor sem args
import lombok.AllArgsConstructor;   // Adicione se estiver usando Lombok para construtor com todos args

import java.math.BigDecimal; // <-- IMPORTANTE: Importar BigDecimal

@Data // Anotação do Lombok para getters, setters, toString, equals, hashCode
@NoArgsConstructor // Construtor sem argumentos (bom para JPA)
@AllArgsConstructor // Construtor com todos os argumentos (útil para testes ou inicialização)
@Entity // Marca esta classe como uma entidade JPA
public class Produto {
    @Id // Marca o campo 'id' como chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura a geração automática do ID pelo banco de dados
    private Long id;

    private String nome;
    private String descricao;

    private BigDecimal preco; // <-- CORREÇÃO: DEVE SER BigDecimal

    @Enumerated(EnumType.STRING) // <-- CORREÇÃO: Mapeia o Enum como String no banco de dados
    private Categoria categoria; // <-- CORREÇÃO: DEVE SER do tipo Categoria (seu Enum)

    private Boolean disponibilidade;
    private String imagem;

    // Se você não usa @NoArgsConstructor e @AllArgsConstructor do Lombok,
    // precisará adicionar os construtores manualmente aqui.
}