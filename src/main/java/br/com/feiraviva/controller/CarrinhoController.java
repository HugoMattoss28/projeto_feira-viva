package br.com.feiraviva.controller;

import br.com.feiraviva.dto.*;
import br.com.feiraviva.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carrinho")
@Tag(name = "Carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @GetMapping
    @Operation(summary = "Obter carrinho do cliente",
            description = "Retorna itens, cupom, estratégia de frete e totais calculados.")
    @ApiResponse(responseCode = "200", description = "Carrinho retornado (pode estar vazio)")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public CarrinhoResponseDTO obter(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId) {
        return carrinhoService.obter(clienteId);
    }

    @PostMapping("/itens")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar item ao carrinho",
            description = "Aplica a regra R1 (estoque); soma quantidade se o produto já estiver no carrinho.")
    @ApiResponse(responseCode = "201", description = "Item adicionado")
    @ApiResponse(responseCode = "400", description = "DTO inválido (quantidade < 1)")
    @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado")
    @ApiResponse(responseCode = "409", description = "Produto sem estoque")
    public CarrinhoResponseDTO adicionar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Valid @RequestBody ItemCarrinhoDTO dto) {
        return carrinhoService.adicionarItem(clienteId, dto);
    }

    @PostMapping("/cupom")
    @Operation(summary = "Aplicar cupom de desconto", description = "Aplica um cupom usando o padrão Factory (ex: FEIRA10, BEMVINDO).")
    @ApiResponse(responseCode = "200", description = "Cupom aplicado com sucesso")
    @ApiResponse(responseCode = "400", description = "Cupom inválido ou expirado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public CarrinhoResponseDTO aplicarCupom(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Parameter(description = "Código do cupom", required = true, example = "FEIRA10")
            @RequestParam String codigo) {
        return carrinhoService.aplicarCupom(clienteId, codigo);
    }

    @DeleteMapping("/cupom")
    @Operation(summary = "Remover cupom de desconto", description = "Remove qualquer cupom atualmente aplicado ao carrinho.")
    @ApiResponse(responseCode = "200", description = "Cupom removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public CarrinhoResponseDTO removerCupom(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId) {
        return carrinhoService.removerCupom(clienteId);
    }

    @PostMapping("/frete")
    @Operation(summary = "Definir estratégia de frete", description = "Define a modalidade de entrega (PADRAO, FIXO, RETIRADA) usando o padrão Strategy.")
    @ApiResponse(responseCode = "200", description = "Estratégia de frete aplicada")
    @ApiResponse(responseCode = "400", description = "Tipo de frete desconhecido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public CarrinhoResponseDTO definirFrete(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @RequestParam Long clienteId,
            @Parameter(description = "Tipo do frete", required = true, example = "PADRAO")
            @RequestParam String tipo) {
        return carrinhoService.definirEstrategiaFrete(clienteId, tipo);
    }
}