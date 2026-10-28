package br.com.feiraviva.dto;

public record TokenResponseDTO(
        String token, String tipo, long expiraEmMs,
        Long clienteId, String email) { }

