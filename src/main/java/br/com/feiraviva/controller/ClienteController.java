package br.com.feiraviva.controller;

import br.com.feiraviva.dto.*;
import br.com.feiraviva.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Cadastro e endereços")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar cliente", description = "Cria um novo registro de cliente na plataforma.")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @ApiResponse(responseCode = "400", description = "DTO de cliente inválido")
    public ClienteResponseDTO criar(
            @Valid @RequestBody ClienteDTO dto) {
        return clienteService.criar(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados cadastrais do cliente e sua lista de endereços.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ClienteResponseDTO buscar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathVariable Long id) {
        return clienteService.buscar(id);
    }

    @PostMapping("/{id}/enderecos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar endereço", description = "Vincula um novo endereço de entrega ao perfil do cliente.")
    @ApiResponse(responseCode = "201", description = "Endereço adicionado com sucesso")
    @ApiResponse(responseCode = "400", description = "DTO de endereço inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public void adicionarEndereco(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EnderecoDTO dto) {

        clienteService.adicionarEndereco(id, dto);
    }
}