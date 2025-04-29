package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.model.Role;
import br.com.alura.AluraFake.user.model.User;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record NewUserDTO(
        @NotNull
        @Length(min = 3, max = 50)
        String name,

        @NotBlank
        @Email
        String email,

        @NotNull
        Role role,

        @Pattern(regexp = "^$|^.{6}$", message = "Password must be exactly 6 characters long if provided")
        String password
) {
    public User toModel() {
        return new User(name, email, role);
    }
}
