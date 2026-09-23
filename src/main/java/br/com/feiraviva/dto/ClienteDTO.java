package br.com.feiraviva.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "Dados para cadastro de um novo cliente")
public record ClienteDTO(
        @Schema(description = "Nome completo do cliente", example = "João da Silva", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(min = 3, max = 120)
        String nome,

        @Schema(description = "Endereço de e-mail válido que servirá de login", example = "joao@exemplo.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Email
        String email,

        @Schema(description = "Senha de acesso ao sistema", example = "senhaSegura123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(min = 6, max = 60)
        String senha,

        @Schema(description = "Telefone de contato (opcional)", example = "(11) 99999-9999")
        String telefone,

        @Schema(description = "Lista de endereços vinculados ao cliente")
        // A linha que faltava para receber os endereços do Postman
        List<EnderecoDTO> enderecos
) { }