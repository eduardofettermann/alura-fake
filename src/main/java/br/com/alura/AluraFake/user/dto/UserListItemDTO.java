package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.model.UserRole;
import br.com.alura.AluraFake.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public record UserListItemDTO(
        @Schema(description = "Nome do usuário", example = "Eduardo Fettermann")
        String name,
        @Schema(description = "Email do usuário", example = "eduardo@alura.com")
        String email,
        @Schema(description = "Papel do usuário no sistema", example = "STUDENT", allowableValues = {"STUDENT", "INSTRUCTOR"})
        UserRole role
) implements Serializable {

    public UserListItemDTO(User user) {
        this(user.getName(), user.getEmail(), user.getRole());
    }
}
