package br.com.feiraviva.controller;

import br.com.feiraviva.dto.*;
import br.com.feiraviva.exception.RegraDeNegocioException;
import br.com.feiraviva.security.ClienteAutenticado;
import br.com.feiraviva.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados cadastrais do cliente e sua lista de endereços (apenas o próprio perfil).")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "409", description = "Tentativa de consultar perfil de outro cliente")
    public ClienteResponseDTO buscar(
            @Parameter(description = "ID do cliente", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal ClienteAutenticado cliente) {
        if (!cliente.id().equals(id)) {
            throw new RegraDeNegocioException("Você só pode consultar o próprio perfil");
        }
        return clienteService.buscar(id);
    }

    @PostMapping("/{id}/enderecos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar endereço", description = "Vincula um novo endereço de entrega ao perfil do cliente autenticado.")
    @ApiResponse(responseCode = "201", description = "Endereço adicionado com sucesso")
    @ApiResponse(responseCode = "400", description = "DTO de endereço inválido")
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "409", description = "Tentativa de alterar perfil de outro cliente")
    public void adicionarEndereco(
          @Parameter(description = "ID do cliente", required = true, example = "1")
          @PathVariable Long id,
          @Parameter(hidden = true) @AuthenticationPrincipal ClienteAutenticado cliente,
          @Valid @RequestBody EnderecoDTO dto) {
        if (!cliente.id().equals(id)) {
            throw new RegraDeNegocioException("Você só pode adicionar endereço ao próprio perfil");
        }
        clienteService.adicionarEndereco(id, dto);
    }
}
