package com.example.menubackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger; // Importe o Logger
import org.slf4j.LoggerFactory; // Importe o LoggerFactory

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class); // Instância do Logger

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.info("Entrando no JwtAuthenticationFilter para requisição: " + request.getRequestURI()); // Log de entrada

        try {
            // Extrai o token JWT do cabeçalho Authorization
            String jwt = getJwtFromRequest(request);

            // Se o token JWT existe e é válido
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromJWT(jwt);
                // Extrai as roles diretamente do token JWT (já devem ter o prefixo "ROLE_")
                List<String> roles = jwtTokenProvider.getRolesFromJWT(jwt);

                logger.info("Token JWT Válido para usuário: " + username + " com roles: " + roles); // Log: token válido e roles

                // Converte as strings de roles para GrantedAuthority
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Cria um objeto UserDetails com as informações do token, sem ir ao banco de dados
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        username,  // O username (email) do token
                        "",        // Senha vazia, pois a autenticação já foi feita via token
                        authorities // As autoridades (roles) do usuário
                );

                // Cria o objeto de autenticação e o define no SecurityContextHolder
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Opcional: Adiciona detalhes da requisição ao objeto de autenticação
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Autenticação definida no SecurityContextHolder para: " + SecurityContextHolder.getContext().getAuthentication().getName()); // Log: autenticação definida

            } else {
                // Loga se o token for nulo ou inválido. Isso pode ser esperado para rotas permitAll().
                logger.warn("Token JWT nulo ou inválido para requisição: " + request.getRequestURI());
            }
        } catch (Exception ex) {
            // Captura e loga quaisquer exceções durante o processo do filtro JWT
            logger.error("ERRO GRAVE no JwtAuthenticationFilter ao definir autenticação para " + request.getRequestURI() + ": " + ex.getMessage(), ex);
        }

        logger.info("Saindo do JwtAuthenticationFilter para requisição: " + request.getRequestURI()); // Log de saída
        filterChain.doFilter(request, response); // Continua a cadeia de filtros
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}