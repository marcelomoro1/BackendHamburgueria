package com.example.menubackend.service;

import com.example.menubackend.dto.ItemPedidoResponseDTO;
import com.example.menubackend.dto.PedidoResponseDTO;
import com.example.menubackend.model.Carrinho;
import com.example.menubackend.model.ItemCarrinho;
import com.example.menubackend.model.ItemPedido;
import com.example.menubackend.model.Pedido;
import com.example.menubackend.model.StatusPedido; // Certifique-se de que StatusPedido tem PENDENTE
import com.example.menubackend.model.User;
import com.example.menubackend.repository.CarrinhoRepository;
import com.example.menubackend.repository.ItemCarrinhoRepository;
import com.example.menubackend.repository.ItemPedidoRepository;
import com.example.menubackend.repository.PedidoRepository;
import com.example.menubackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional // Garante que toda a operação de finalização seja atômica
    public PedidoResponseDTO finalizarPedido(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));

        Carrinho carrinho = carrinhoRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário: " + userId));

        if (carrinho.getItens().isEmpty()) {
            throw new RuntimeException("O carrinho está vazio. Não é possível finalizar o pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(user);
        pedido.setDataPedido(LocalDateTime.now());
        // --- MUDANÇA AQUI: Status inicial PENDENTE ---
        pedido.setStatus(StatusPedido.PENDENTE);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemCarrinho itemCarrinho : carrinho.getItens()) {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido); // Associa ao pedido atual
            itemPedido.setProduto(itemCarrinho.getProduto());
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(itemCarrinho.getPrecoUnitario());

            valorTotal = valorTotal.add(itemPedido.getPrecoUnitario().multiply(BigDecimal.valueOf(itemPedido.getQuantidade())));

            pedido.getItens().add(itemPedido); // Adiciona o item ao pedido
        }

        pedido.setValorTotal(valorTotal);

        Pedido savedPedido = pedidoRepository.save(pedido); // Salva o pedido e os itens em cascata

        // Após finalizar o pedido, limpa o carrinho
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho); // Salva o carrinho vazio

        return convertToDto(savedPedido);
    }

    // --- NOVO MÉTODO: Atualizar Status do Pedido (para ADMIN) ---
    @Transactional
    public PedidoResponseDTO updatePedidoStatus(Long pedidoId, StatusPedido newStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        // Você pode adicionar validações aqui, por exemplo, se o status atual permite a transição para newStatus.
        // Ex: Não permitir mudar de CANCELADO para FINALIZADO.
        pedido.setStatus(newStatus);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        return convertToDto(updatedPedido);
    }

    // Obter um pedido por ID (para admin ou cliente que queira ver detalhes)
    public Optional<PedidoResponseDTO> getPedidoById(Long id) {
        return pedidoRepository.findById(id)
                .map(this::convertToDto);
    }

    // Obter todos os pedidos de um usuário (para a área do cliente)
    public List<PedidoResponseDTO> getPedidosByUserId(Long userId) {
        return pedidoRepository.findByUsuarioIdOrderByDataPedidoDesc(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Obter todos os pedidos (para a área administrativa)
    public List<PedidoResponseDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Método auxiliar para converter Entidade para DTO (Pedido e seus itens)
    private PedidoResponseDTO convertToDto(Pedido pedido) {
        PedidoResponseDTO pedidoDTO = new PedidoResponseDTO();
        pedidoDTO.setId(pedido.getId());
        pedidoDTO.setUserId(pedido.getUsuario().getId());
        pedidoDTO.setUserName(pedido.getUsuario().getName());
        pedidoDTO.setDataPedido(pedido.getDataPedido());
        pedidoDTO.setStatus(pedido.getStatus());
        pedidoDTO.setValorTotal(pedido.getValorTotal());

        List<ItemPedidoResponseDTO> itemDTOs = pedido.getItens().stream()
                .map(item -> {
                    ItemPedidoResponseDTO itemPedidoDTO = new ItemPedidoResponseDTO();
                    itemPedidoDTO.setId(item.getId());
                    itemPedidoDTO.setProdutoId(item.getProduto().getId());
                    itemPedidoDTO.setNomeProduto(item.getProduto().getNome());
                    itemPedidoDTO.setImagemProduto(item.getProduto().getImagem());
                    itemPedidoDTO.setQuantidade(item.getQuantidade());
                    itemPedidoDTO.setPrecoUnitario(item.getPrecoUnitario());
                    itemPedidoDTO.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
                    return itemPedidoDTO;
                })
                .collect(Collectors.toList());
        pedidoDTO.setItens(itemDTOs);

        return pedidoDTO;
    }
}