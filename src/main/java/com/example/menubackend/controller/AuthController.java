package com.example.menubackend.controller;

import com.example.menubackend.model.Role; 
import com.example.menubackend.model.User;
import com.example.menubackend.payload.ApiResponse;
import com.example.menubackend.payload.JwtAuthenticationResponse;
import com.example.menubackend.dto.UserLoginDTO; 
import com.example.menubackend.dto.UserRegisterDTO;
import com.example.menubackend.repository.UserRepository;
import com.example.menubackend.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

        String jwt = tokenProvider.generateToken(authentication);
        logger.info("Token JWT gerado para o usuário {}: {}", userLoginDTO.getEmail(), (jwt != null && !jwt.isEmpty() ? "Token gerado com sucesso." : "TOKEN ESTÁ NULO OU VAZIO!"));
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt)); 
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        // Validação se o username já existe (usando o email como username)
        if (userRepository.existsByUsername(userRegisterDTO.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Nome de usuário (baseado no email) já está em uso!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Validação se o email já existe
        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email já está em uso!"),
                    HttpStatus.BAD_REQUEST);
        }

        // 
        User user = new User(
                userRegisterDTO.getNome(), 
                userRegisterDTO.getEmail(), // Usa getEmail() do UserRegisterDTO como username
                userRegisterDTO.getEmail(), // Usa getEmail() do UserRegisterDTO como email
                userRegisterDTO.getPassword()
        );


        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Role padrão CLIENTE
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CLIENTE);
        user.setRoles(roles);

        User result = userRepository.save(user);

        //URI de localização para a resposta
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Usuário registrado com sucesso!"));
    }
}
