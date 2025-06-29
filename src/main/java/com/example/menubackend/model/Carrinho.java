package com.example.menubackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Carrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User usuario;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // O valorTotal NÃO será mais persistido aqui. Ele será calculado no serviço e no DTO.
    // private BigDecimal valorTotal; // <--- REMOVA ESTA LINHA SE ELA EXISTIR!

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinho> itens = new ArrayList<>();
}