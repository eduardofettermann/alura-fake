package br.com.alura.AluraFake.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginResponseDTO(
        @Schema(description = "Token JWT de autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
}
