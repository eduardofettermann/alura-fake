package br.com.alura.AluraFake.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserAuthenticationDTO(
        @Schema(description = "Email do usuário", example = "juliano@alura.com.br")
        String email,
        @Schema(description = "Senha do usuário", example = "senha123")
        String password) {
}
