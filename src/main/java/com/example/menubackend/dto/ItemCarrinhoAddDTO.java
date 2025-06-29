package com.example.menubackend.dto;

import lombok.Data;

@Data
public class ItemCarrinhoAddDTO {
    private Long produtoId;
    private Integer quantidade;
}