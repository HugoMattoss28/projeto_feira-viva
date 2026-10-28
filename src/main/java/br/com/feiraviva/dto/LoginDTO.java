package br.com.feiraviva.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação via JWT")
public record LoginDTO(
        @Schema(description = "E-mail do cliente", example = "maria@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank @Email String email,
        @Schema(description = "Senha do cliente", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String senha) { }

