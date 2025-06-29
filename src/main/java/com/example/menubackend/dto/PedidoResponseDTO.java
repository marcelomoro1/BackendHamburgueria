package com.example.menubackend.dto;

import com.example.menubackend.model.StatusPedido;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoResponseDTO {
    private Long id; // ID do Pedido
    private Long userId; // ID do usuário que fez o pedido
    private String userName; // Nome do usuário para exibição no admin
    private LocalDateTime dataPedido;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private List<ItemPedidoResponseDTO> itens;
}