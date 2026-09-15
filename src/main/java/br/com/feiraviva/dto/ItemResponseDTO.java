package br.com.feiraviva.dto;

import java.math.BigDecimal;

public record ItemResponseDTO(
        Long id, Long produtoId, String nomeProduto,
        int quantidade, BigDecimal precoUnitario, BigDecimal subtotal) { }