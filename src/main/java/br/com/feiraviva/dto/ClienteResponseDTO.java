package br.com.feiraviva.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Dados de retorno do cliente")
public record ClienteResponseDTO(
        @Schema(description = "ID do cliente", example = "1") Long id,
        @Schema(description = "Nome completo", example = "João da Silva") String nome,
        @Schema(description = "E-mail cadastrado", example = "joao@exemplo.com") String email,
        @Schema(description = "Telefone de contato", example = "(11) 99999-9999") String telefone,
        @Schema(description = "Lista de endereços do cliente") List<EnderecoDTO> enderecos
) { }