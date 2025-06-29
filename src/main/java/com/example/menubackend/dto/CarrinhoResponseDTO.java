package com.example.menubackend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CarrinhoResponseDTO {
    private Long id; // ID do Carrinho
    private String userName; //
    private Long userId; // ID do usuário do carrinho
    private List<ItemCarrinhoResponseDTO> itens;
    private BigDecimal valorTotal; // Soma dos subtotais dos itens
}