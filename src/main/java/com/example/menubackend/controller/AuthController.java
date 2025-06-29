package com.example.menubackend.controller;

import com.example.menubackend.model.Role; // Seu Enum de Roles
import com.example.menubackend.model.User;
import com.example.menubackend.payload.ApiResponse;
import com.example.menubackend.payload.JwtAuthenticationResponse;
import com.example.menubackend.dto.UserLoginDTO; // Corrigido 'dtos' para 'dto'
import com.example.menubackend.dto.UserRegisterDTO; // Corrigido 'dtos' para 'dto'
import com.example.menubackend.repository.UserRepository;
import com.example.menubackend.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // CORRETO!
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // Adicione esta linha

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginDTO userLoginDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.getEmail(), // Usa getEmail() do UserLoginDTO
                        userLoginDTO.getPassword() // Usa getPassword() do UserLoginDTO
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication); // Chamada correta do método
        logger.info("Token JWT gerado para o usuário {}: {}", userLoginDTO.getEmail(), (jwt != null && !jwt.isEmpty() ? "Token gerado com sucesso." : "TOKEN ESTÁ NULO OU VAZIO!"));
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt)); // Construtor de JwtAuthenticationResponse corrigido
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        // Validação se o username já existe. Usamos o email como username para a Model User.
        if (userRepository.existsByUsername(userRegisterDTO.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Nome de usuário (baseado no email) já está em uso!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Validação se o email já existe
        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email já está em uso!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Criar a conta do novo usuário a partir do DTO de registro
        // Preenchendo o 'username' da Model User com o 'email' do DTO para manter unicidade.
        User user = new User(
                userRegisterDTO.getNome(), // Usa getNome() do UserRegisterDTO
                userRegisterDTO.getEmail(), // Usa getEmail() do UserRegisterDTO como username
                userRegisterDTO.getEmail(), // Usa getEmail() do UserRegisterDTO como email
                userRegisterDTO.getPassword() // Usa getPassword() do UserRegisterDTO
        );

        // Codificar a senha antes de salvar no banco de dados
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Atribuir a ROLE padrão (CLIENTE) diretamente do Enum Role
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CLIENTE); // CORRETO com seu Enum Role
        user.setRoles(roles);

        // Salvar o usuário no banco de dados
        User result = userRepository.save(user);

        // Constrói a URI de localização para a resposta
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        // Retorna uma resposta de sucesso
        return ResponseEntity.created(location).body(new ApiResponse(true, "Usuário registrado com sucesso!"));
    }
}