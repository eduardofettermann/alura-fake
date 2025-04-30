package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegisterDTO(
        @NotBlank
        String name,
        @NotBlank
        @Email
        String email,
        @NotNull
        Role role,
        @NotNull
        String password
) {
}
