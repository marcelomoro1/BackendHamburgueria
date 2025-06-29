package com.example.menubackend.security;

import com.example.menubackend.model.User; // Importe sua entidade User
import com.example.menubackend.repository.UserRepository; // Importe seu UserRepository
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-in-ms}")
    private int jwtExpirationInMs;

    private Key key;

    // Injete o UserRepository
    private final UserRepository userRepository;

    public JwtTokenProvider(UserRepository userRepository) { // Adicione ao construtor
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        // Inicializa a chave de assinatura decodificando o segredo Base64
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    public String generateToken(Authentication authentication) {
        // Obtém os detalhes do usuário autenticado fornecidos pelo Spring Security
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Inicializa o userId como null. Ele será preenchido se o usuário for encontrado.
        Long userId = null;

        // Busca o usuário completo no banco de dados usando o email (que é o username do UserDetails)
        // Isso é feito para obter o ID da sua entidade User.
        User user = userRepository.findByEmail(userPrincipal.getUsername()).orElse(null);

        // Se o usuário for encontrado, extrai o ID dele
        if (user != null) {
            userId = user.getId(); // Assume que sua entidade User tem um método getId()
        }

        // Mapeia as GrantedAuthority (roles) do UserDetails para uma lista de Strings
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date now = new Date(); // Data atual
        // Calcula a data de expiração do token
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Constrói o token JWT com as claims (subject, roles, userId, data de emissão e expiração)
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Define o "subject" do token (geralmente o username/email)
                .claim("roles", roles) // Adiciona as roles do usuário como uma claim personalizada
                .claim("userId", userId) // Adiciona o ID do usuário como uma claim personalizada
                .setIssuedAt(new Date()) // Define a data de emissão do token
                .setExpiration(expiryDate) // Define a data de expiração do token
                .signWith(key, SignatureAlgorithm.HS512) // Assina o token com a chave e algoritmo HS512
                .compact(); // Constrói e serializa o token para uma string compacta
    }

    // Método para extrair as roles do token JWT
    public List<String> getRolesFromJWT(String token) {
        // Parsifica o token para obter as claims (corpo do token)
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // Usa a mesma chave para verificar a assinatura
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Retorna a lista de roles da claim "roles"
        // É feito um cast para List<String> pois as claims são retornadas como Object
        return (List<String>) claims.get("roles");
    }

    // Método para extrair o username (subject) do token JWT
    public String getUsernameFromJWT(String token) {
        // Parsifica o token para obter as claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Retorna o "subject" do token, que é o username (email)
        return claims.getSubject();
    }

    // Método para obter o userId do token JWT
    public Long getUserIdFromJWT(String token) {
        // Parsifica o token para obter as claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Tenta obter o "userId" da claim e o converte para Long
        // Se a claim não existir ou não for um número, pode retornar null ou lançar exceção
        return claims.get("userId", Long.class);
    }

    // Método para validar um token JWT
    public boolean validateToken(String authToken) {
        try {
            // Tenta parsificar e validar a assinatura do token
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true; // Token válido
        } catch (SignatureException ex) {
            // Erro se a assinatura do token for inválida
            System.out.println("Assinatura JWT inválida: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            // Erro se o token JWT estiver malformado (ex: formato incorreto)
            System.out.println("Token JWT malformado: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            // Erro se o token JWT estiver expirado
            System.out.println("Token JWT expirado: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            // Erro se o tipo de token JWT não for suportado
            System.out.println("Token JWT não suportado: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Erro se o token JWT for uma string vazia ou nula
            System.out.println("Argumento ilegal para token JWT: " + ex.getMessage());
        }
        return false; // Token inválido por qualquer um dos motivos acima
    }
}