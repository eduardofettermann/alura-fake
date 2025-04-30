package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegisterDTO(
        @Schema(description = "Nome do usuário", example = "Juliano Tubino")
        @NotBlank
        String name,
        @Schema(description = "Email do usuário", example = "juliano@alura.com.br")
        @NotBlank
        @Email
        String email,
        @Schema(description = "Papel do usuário no sistema", example = "INSTRUCTOR", allowableValues = {"STUDENT", "INSTRUCTOR"})
        @NotNull
        UserRole role,
        @Schema(description = "Senha do usuário (min 6 caracteres)", example = "senha123")
        @NotNull
        String password
) {
}
