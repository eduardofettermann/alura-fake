package br.com.alura.AluraFake.user.model;

import br.com.alura.AluraFake.util.PasswordGeneration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Getter
    private String name;
    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;
    @Getter
    private String email;
    // Por questões didáticas, a senha será armazenada em texto plano.
    @Getter
    private String password;

    public User(String name, String email, Role role, String password) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, Role role) {
        this(name, email, role, PasswordGeneration.generatePassword());
    }

    public boolean isInstructor() {
        return Role.INSTRUCTOR.equals(this.role);
    }
}
