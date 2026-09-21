package br.com.feiraviva.dto;

import jakarta.validation.constraints.*;
import java.util.List; // Não se esqueça deste import

public record ClienteDTO(
        @NotBlank
        @Size(min = 3, max = 120)
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 6, max = 60)
        String senha,

        String telefone,

        // A linha que faltava para receber os endereços do Postman
        List<EnderecoDTO> enderecos
) { }