package br.com.alura.AluraFake.user.model;

import br.com.alura.AluraFake.util.PasswordGeneration;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private final LocalDateTime createdAt = LocalDateTime.now();
    @Getter
    private String name;
    @Getter
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Getter
    private String email;
    // Por questões didáticas, a senha será armazenada em texto plano.
    @Getter
    private String password;

    public User(String name, String email, UserRole role, String password) {
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, UserRole role) {
        this(name, email, role, PasswordGeneration.generatePassword());
    }

    public boolean isInstructor() {
        return Role.INSTRUCTOR.equals(this.role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    public String getPassword() {
        return password;

    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
