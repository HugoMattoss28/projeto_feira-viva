package br.com.feiraviva.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Dados de endereço")
public record EnderecoDTO(
        @Schema(description = "CEP (apenas números)", example = "15700000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Pattern(regexp = "\\d{8}")
        String cep,

        @Schema(description = "Nome da rua/avenida", example = "Rua das Flores", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(max = 120)
        String logradouro,

        @Schema(description = "Número", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String numero,

        @Schema(description = "Complemento", example = "Apto 42")
        String complemento,

        @Schema(description = "Bairro", example = "Centro", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String bairro,

        @Schema(description = "Cidade", example = "Santa Salete", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String cidade,

        @Schema(description = "Estado (UF)", example = "SP", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        @Size(min = 2, max = 2)
        String uf
) { }