package br.com.feiraviva.controller;

import br.com.feiraviva.dto.*;
import br.com.feiraviva.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Finalização, histórico e cancelamento")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Finalizar pedido", description = "Transforma o carrinho atual do cliente em um novo pedido, verificando estoque.")
    @ApiResponse(responseCode = "201", description = "Pedido finalizado com sucesso")
    @ApiResponse(responseCode = "400", description = "DTO inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado ou carrinho vazio")
    @ApiResponse(responseCode = "409", description = "Produto sem estoque ou falha na regra de negócio")
    public PedidoResponseDTO finalizar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Valid @RequestBody PedidoRequestDTO dto) {
        return pedidoService.finalizar(clienteId, dto);
    }

    @GetMapping
    @Operation(summary = "Histórico de pedidos", description = "Retorna a lista de todos os pedidos já realizados por um cliente.")
    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public List<PedidoResponseDTO> historico(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId) {
        return pedidoService.historico(clienteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar detalhes de um pedido", description = "Retorna os dados completos de um pedido específico pertencente ao cliente.")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente ou Pedido não encontrado")
    public PedidoResponseDTO buscar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Parameter(description = "ID do pedido", required = true, example = "100")
            @PathVariable Long id) {
        return pedidoService.buscar(clienteId, id);
    }

    @PostMapping("/{id}/cancelamento")
    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido que está com status CRIADO e devolve os itens ao estoque (Regra R4).")
    @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente ou Pedido não encontrado")
    @ApiResponse(responseCode = "409", description = "O pedido não pode ser cancelado (status não é CRIADO)")
    public PedidoResponseDTO cancelar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Parameter(description = "ID do pedido a ser cancelado", required = true, example = "100")
            @PathVariable Long id) {
        return pedidoService.cancelar(clienteId, id);
    }
}