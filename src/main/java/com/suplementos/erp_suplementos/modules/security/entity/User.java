package com.suplementos.erp_suplementos.modules.security.entity;

import com.suplementos.erp_suplementos.modules.security.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Entity(name = "User")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O login será o e-mail do usuário (precisa ser único)
    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // --- MÉTODOS OBRIGATÓRIOS DO SPRING SECURITY (USERDETAILS) ---

    // Define as permissões baseadas na Role
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            // O Admin tem a role de ADMIN e também a de USER normal
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getUsername() {
        return login; // Nosso username é o email/login
    }

    @Override
    public String getPassword() {
        return password; // O hash que o BCrypt vai gerar
    }

    // Os 4 métodos abaixo representam o status da conta.
    // Por enquanto deixaremos 'true', mas no futuro, se houver um ataque de Força Bruta,
    // podemos alterar o 'isAccountNonLocked' para 'false' e bloquear o hacker!

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}