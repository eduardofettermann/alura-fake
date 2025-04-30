package br.com.alura.AluraFake.user.dto;

import br.com.alura.AluraFake.user.model.UserRole;
import br.com.alura.AluraFake.user.model.User;

import java.io.Serializable;

public record UserListItemDTO(
        String name,
        String email,
        UserRole role
) implements Serializable {

    public UserListItemDTO(User user) {
        this(user.getName(), user.getEmail(), user.getRole());
    }
}
