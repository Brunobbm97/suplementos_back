package com.suplementos.erp_suplementos.modules.security.config;

import com.suplementos.erp_suplementos.modules.security.filter.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter; // <-- 1. Injetamos o nosso filtro aqui

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. LIGANDO O CORS (Dizendo ao Spring para usar a configuração abaixo)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 1. Desabilitamos a proteção CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Definimos que a API não guardará estado (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Regras de Autorização de Rotas
                .authorizeHttpRequests(auth -> auth
                        // Liberamos o endpoint de login para qualquer pessoa tentar acessar
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Qualquer outra requisição (como o Dashboard e o PDV) exigirá autenticação
                        .anyRequest().authenticated()
                )
                //  Avisamos: "Execute o meu filtro JWT ANTES do filtro padrão de usuário/senha do Spring"
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Quem tem permissão para acessar a API? (A porta do seu Angular)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // 2. Quais métodos são permitidos? (IMPORTANTE: O 'OPTIONS' precisa estar aqui!)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Quais cabeçalhos o Angular pode mandar? (O Authorization do nosso Interceptor)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica essa regra para o ERP inteiro

        return source;
    }


}