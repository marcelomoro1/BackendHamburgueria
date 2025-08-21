package com.example.menubackend.controller;

import com.example.menubackend.dto.CarrinhoResponseDTO;
import com.example.menubackend.dto.ItemCarrinhoAddDTO;
import com.example.menubackend.dto.ItemCarrinhoUpdateDTO;
import com.example.menubackend.service.CarrinhoService;
import com.example.menubackend.repository.UserRepository;
import com.example.menubackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    private static final Logger logger = LoggerFactory.getLogger(CarrinhoController.class);

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired 
    private UserRepository userRepository;

    @GetMapping("/meu")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<CarrinhoResponseDTO> getMeuCarrinho(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Entrando em getMeuCarrinho. Usuário autenticado (username/email do UserDetails): {}", userDetails.getUsername());
        logger.info("Authorities do usuário: {}", userDetails.getAuthorities());

        // Verifique o SecurityContextHolder diretamente (para depuração)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("SecurityContextHolder: Autenticado como {}", authentication.getName());
            logger.info("SecurityContextHolder: Roles: {}", authentication.getAuthorities());
        } else {
            logger.warn("SecurityContextHolder: Nenhuma autenticação ou não autenticado ao entrar no controller!");
        }

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados para o email: " + userDetails.getUsername()));

        Long userId = user.getId(); 
        logger.info("ID do usuário obtido do banco de dados: {}", userId);

        CarrinhoResponseDTO carrinhoDTO = carrinhoService.getOrCreateCarrinho(userId);
        return ResponseEntity.ok(carrinhoDTO);
    }

    /**
     * Endpoint para adicionar um item ao carrinho do usuário logado.
     * Requer que o usuário tenha a role 'CLIENTE' ou 'ADMIN'.
     *
     * @param userDetails O UserDetails padrão injetado.
     * @param itemAddDTO O DTO contendo as informações do item a ser adicionado.
     * @return ResponseEntity com o CarrinhoResponseDTO atualizado e status CREATED.
     */
    @PostMapping("/adicionar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<CarrinhoResponseDTO> addItemToCarrinho( 
                                                                  @AuthenticationPrincipal UserDetails userDetails,
                                                                  @Valid @RequestBody ItemCarrinhoAddDTO itemAddDTO) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados para o email: " + userDetails.getUsername()));
        Long userId = user.getId();
        CarrinhoResponseDTO carrinhoDTO = carrinhoService.addItemToCarrinho(userId, itemAddDTO); 
        return new ResponseEntity<>(carrinhoDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint para atualizar a quantidade de um item existente no carrinho do usuário logado.
     * Usuario precisa ter 'CLIENTE' ou 'ADMIN' como role.
     *
     * @param userDetails O UserDetails padrão injetado.
     * @param itemId O ID do item no carrinho a ser atualizado.
     * @param updateDto O DTO contendo a nova quantidade.
     * @return ResponseEntity com o CarrinhoResponseDTO atualizado e status OK.
     */
    @PutMapping("/atualizar/{itemId}") 
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<CarrinhoResponseDTO> updateItemQuantity( 
                                                                   @AuthenticationPrincipal UserDetails userDetails,
                                                                   @PathVariable Long itemId, 
                                                                   @Valid @RequestBody ItemCarrinhoUpdateDTO updateDto) { 

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados para o email: " + userDetails.getUsername()));
        Long userId = user.getId();
        CarrinhoResponseDTO carrinhoDTO = carrinhoService.updateItemQuantity(userId, itemId, updateDto);
        return ResponseEntity.ok(carrinhoDTO);
    }


    @DeleteMapping("/remover/{itemId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Void> removeItemFromCarrinho( 
                                                        @AuthenticationPrincipal UserDetails userDetails,
                                                        @PathVariable Long itemId) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados para o email: " + userDetails.getUsername()));
        Long userId = user.getId();
        CarrinhoResponseDTO carrinhoDTO = carrinhoService.removeItemFromCarrinho(userId, itemId); 
        return ResponseEntity.noContent().build();
    }
    

    @DeleteMapping("/limpar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Void> clearCarrinho( 
                                               @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados para o email: " + userDetails.getUsername()));
        Long userId = user.getId();
        carrinhoService.clearCarrinho(userId);
        return ResponseEntity.noContent().build();
    }
}
