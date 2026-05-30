package com.suplementos.erp_suplementos.modules.security.controller;

import com.suplementos.erp_suplementos.modules.security.dto.AuthenticationDTO;
import com.suplementos.erp_suplementos.modules.security.dto.LoginResponseDTO;
import com.suplementos.erp_suplementos.modules.security.dto.RegisterDTO;
import com.suplementos.erp_suplementos.modules.security.entity.User;
import com.suplementos.erp_suplementos.modules.security.repository.UserRepository;
import com.suplementos.erp_suplementos.modules.security.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // O "Gerente" de autenticação do Spring

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Injetamos o BCrypt que configuramos antes

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        // Cria um token de verificação apenas com login e senha pura (ainda não validado)
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());

        // Aqui a mágica acontece: O Spring pega a senha digitada, faz o hash Bcrypt e cruza com o banco.
        // Se a senha estiver errada, ele joga uma exceção sozinho e retorna erro 403.
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Se passar da linha acima, a senha está certa. Vamos gerar o JWT!
        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        // 1. Verifica se o e-mail já existe no banco
        if (this.userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }

        // 2. Aqui a mágica acontece: O próprio Java vai gerar o Hash BCrypt com segurança!
        String encryptedPassword = passwordEncoder.encode(data.password());

        // 3. Cria e salva o usuário
        User newUser = new User(null, data.login(), encryptedPassword, data.role());
        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}