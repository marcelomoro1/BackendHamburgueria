package com.example.menubackend.controller;

import com.example.menubackend.dto.PedidoRequestDTO; // Para a requisição de pedido (se houver, como finalizar um carrinho)
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
@RequestMapping("/api/pedidos") // URL base para endpoints de pedidos
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem (em desenvolvimento)
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UserRepository userRepository; // Para buscar o ID do usuário pelo email do UserDetails

    /**
     * Endpoint para o ADMIN visualizar TODOS os pedidos da plataforma.
     * Esta é a rota que o GerenciarPedidos do frontend deve chamar.
     * Requer autenticação e role de ADMIN.
     *
     * ALTERAÇÃO CHAVE: Este @GetMapping agora mapeia para /api/pedidos e exige ADMIN.
     */
    @GetMapping // Mapeia para GET /api/pedidos
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoResponseDTO>> getAllPedidosForAdmin() {
        List<PedidoResponseDTO> pedidos = pedidoService.getAllPedidos(); // Certifique-se de ter este método em PedidoService
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Endpoint para finalizar um pedido a partir do carrinho do usuário logado.
     * Requer autenticação e role de CLIENTE.
     * Assumindo que o PedidoService.finalizarPedido lê o carrinho do userId e o transforma em pedido.
     */
    @PostMapping("/finalizar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> finalizarPedido(@AuthenticationPrincipal UserDetails userDetails) {
        // Obtém o usuário logado para extrair o ID
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado."));
        Long userId = user.getId();

        // Chama o serviço para finalizar o pedido
        PedidoResponseDTO pedidoFinalizado = pedidoService.finalizarPedido(userId);
        return ResponseEntity.ok(pedidoFinalizado);
    }

    /**
     * Endpoint para o cliente visualizar seus próprios pedidos.
     * Acessível por CLIENTE ou ADMIN (ADMIN pode usar para ver pedidos de si mesmo se também for cliente).
     */
    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<PedidoResponseDTO>> getMeusPedidos(@AuthenticationPrincipal UserDetails userDetails) {
        // Obtém o usuário logado para extrair o ID
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado."));
        Long userId = user.getId();

        // Chama o serviço para obter os pedidos do usuário
        List<PedidoResponseDTO> pedidos = pedidoService.getPedidosByUserId(userId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Endpoint para obter um pedido específico por ID.
     * Cliente só pode ver seus próprios pedidos. ADMIN pode ver qualquer pedido.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<PedidoResponseDTO> getPedidoById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // Obtém o pedido pelo ID (o serviço deve lançar exceção se não encontrado)
        PedidoResponseDTO pedido = pedidoService.getPedidoById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado."));

        // Obtém o usuário logado para verificar a permissão
        User userLogado = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado."));

        // Verifica se o usuário não é ADMIN
        if (!userLogado.getRoles().stream().anyMatch(role -> role.name().equals("ADMIN"))) {
            // Se não for ADMIN, verifica se o ID do usuário logado é diferente do ID do dono do pedido
            if (!userLogado.getId().equals(pedido.getUserId())) { // Supondo que PedidoResponseDTO tenha getUserId()
                // Se não for ADMIN e não for o dono do pedido, retorna 403 Forbidden
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(pedido);
    }

    /**
     * Endpoint para atualizar o status de um pedido (apenas ADMIN).
     * O 'newStatus' deve ser um nome válido de um enum StatusPedido (ex: "PENDENTE", "FINALIZADO", "CANCELADO").
     */
    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoResponseDTO> updatePedidoStatus(@PathVariable Long id, @RequestParam String newStatus) {
        try {
            // Converte a string newStatus para o enum StatusPedido
            StatusPedido status = StatusPedido.valueOf(newStatus.toUpperCase());
            // Chama o serviço para atualizar o status
            PedidoResponseDTO updatedPedido = pedidoService.updatePedidoStatus(id, status);
            return ResponseEntity.ok(updatedPedido);
        } catch (IllegalArgumentException e) {
            // Se a string newStatus não for um valor válido do enum StatusPedido
            return ResponseEntity.badRequest().body(null); // Pode retornar um DTO de erro mais detalhado
        } catch (RuntimeException e) {
            // Captura RuntimeException do serviço (e.g., pedido não encontrado)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Ou um DTO de erro com a mensagem da exceção
        }
    }


}