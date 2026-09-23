package br.com.feiraviva.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Dados de retorno de um pedido finalizado")
public record PedidoResponseDTO(
        @Schema(description = "ID interno do pedido", example = "1") Long id,
        @Schema(description = "Número de identificação", example = "PED-1234") String numero,
        @Schema(description = "Status atual", example = "CRIADO") String status,
        @Schema(description = "Soma dos itens", example = "70.00") BigDecimal subtotal,
        @Schema(description = "Valor do frete", example = "15.00") BigDecimal frete,
        @Schema(description = "Total final (com descontos e frete)", example = "78.00") BigDecimal total,
        @Schema(description = "Lista de itens do pedido") List<ItemResponseDTO> itens) { }