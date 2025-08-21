package com.example.menubackend.controller;

import com.example.menubackend.dto.PedidoRequestDTO; 
import com.example.menubackend.dto.PedidoResponseDTO;
import com.example.menubackend.model.StatusPedido;
import com.example.menubackend.model.User;
import com.example.menubackend.repository.UserRepository;
import com.example.menubackend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos") 
@CrossOrigin(origins = "*") 
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UserRepository userRepository; 

    /**
     * Endpoint para o ADMIN visualizar TODOS os pedidos da plataforma.
     * Requer autenticação e role de ADMIN.
     */
    @GetMapping // Mapeia para GET /api/pedidos
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedidosForAdmin() {
        List<PedidoResponseDTO> pedidos = pedidoService.getAllPedidos(); 
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Endpoint para finalizar um pedido a partir do carrinho do usuário logado.
     * Requer autenticação e role de CLIENTE.
     */
    @PostMapping("/finalizar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> finalizarPedido(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado."));
        Long userId = user.getId();

        PedidoResponseDTO pedidoFinalizado = pedidoService.finalizarPedido(userId);
        return ResponseEntity.ok(pedidoFinalizado);
    }

    /**
     * Endpoint para o cliente visualizar seus próprios pedidos.
     * Acessível por CLIENTE ou ADMIN.
     */
    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<PedidoResponseDTO>> getMeusPedidos(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado."));
        Long userId = user.getId();

        List<PedidoResponseDTO> pedidos = pedidoService.getPedidosByUserId(userId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Endpoint para obter um pedido específico por ID.
     * Cliente só pode ver seus próprios pedidos.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<PedidoResponseDTO> getPedidoById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        PedidoResponseDTO pedido = pedidoService.getPedidoById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        User userLogado = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado."));

        // Verifica se o usuário não é ADMIN
        if (!userLogado.getRoles().stream().anyMatch(role -> role.name().equals("ADMIN"))) {
            // Se não for ADMIN, verifica se o ID do usuário logado é diferente do ID do dono do pedido
            if (!userLogado.getId().equals(pedido.getUserId())) { 
                // Se não for ADMIN e não for o dono do pedido, retorna 403 Forbidden
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(pedido);
    }

    /**
     * Endpoint para atualizar o status de um pedido (apenas ADMIN).
     */
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> updatePedidoStatus(@PathVariable Long id, @RequestParam String newStatus) {
        try {
            // Converte a string newStatus para o enum StatusPedido
            StatusPedido status = StatusPedido.valueOf(newStatus.toUpperCase());
            PedidoResponseDTO updatedPedido = pedidoService.updatePedidoStatus(id, status);
            return ResponseEntity.ok(updatedPedido);
        } catch (IllegalArgumentException e) {
            // Se a string newStatus não for um valor válido do enum StatusPedido
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            // Captura RuntimeException do serviço(pedido não encontrado)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }
    }


}
