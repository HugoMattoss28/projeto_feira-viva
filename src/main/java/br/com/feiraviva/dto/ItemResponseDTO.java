package br.com.feiraviva.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Dados de um item retornado nas respostas")
public record ItemResponseDTO(
        @Schema(description = "ID do item", example = "1") Long id,
        @Schema(description = "ID do produto", example = "10") Long produtoId,
        @Schema(description = "Nome do produto", example = "Tomate Orgânico") String nomeProduto,
        @Schema(description = "Quantidade no carrinho ou pedido", example = "2") int quantidade,
        @Schema(description = "Preço unitário", example = "5.50") BigDecimal precoUnitario,
        @Schema(description = "Subtotal do item (quantidade * preço)", example = "11.00") BigDecimal subtotal) { }