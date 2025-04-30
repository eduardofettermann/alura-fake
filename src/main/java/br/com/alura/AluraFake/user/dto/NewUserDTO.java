package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.model.UserRole;
import br.com.alura.AluraFake.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record NewUserDTO(
        @Schema(description = "Nome do usuário", example = "Paulo Silveira")
        @NotNull
        @Length(min = 3, max = 50)
        String name,

        @Schema(description = "Email do usuário", example = "joao.silva@email.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "Papel do usuário no sistema", example = "STUDENT", allowableValues = {"STUDENT", "INSTRUCTOR"})
        @NotNull
        UserRole role,

        @Schema(description = "Senha do usuário (min 6 caracteres)", example = "senha123")
        @Pattern(regexp = "^$|^.{6}$", message = "Password must be exactly 6 characters long if provided")
        String password
) {
    public User toModel() {
        return new User(name, email, role);
    }
}
