package com.example.menubackend.service;

import com.example.menubackend.dto.CarrinhoResponseDTO;
import com.example.menubackend.dto.ItemCarrinhoAddDTO;
import com.example.menubackend.dto.ItemCarrinhoResponseDTO;
import com.example.menubackend.dto.ItemCarrinhoUpdateDTO;
import com.example.menubackend.model.Carrinho;
import com.example.menubackend.model.ItemCarrinho;
import com.example.menubackend.model.Produto;
import com.example.menubackend.model.User;
import com.example.menubackend.repository.CarrinhoRepository;
import com.example.menubackend.repository.ItemCarrinhoRepository;
import com.example.menubackend.repository.ProdutoRepository;
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
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UserRepository userRepository;

    // Obter ou criar o carrinho para um usuário
    public CarrinhoResponseDTO getOrCreateCarrinho(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + userId));

        Carrinho carrinho = carrinhoRepository.findByUsuarioId(userId)
                .orElseGet(() -> {
                    Carrinho newCarrinho = new Carrinho();
                    newCarrinho.setUsuario(user);
                    newCarrinho.setDataCriacao(LocalDateTime.now());
                    newCarrinho.setDataAtualizacao(LocalDateTime.now());
                    // O valorTotal não é mais inicializado aqui na ENTIDADE, pois foi removido dela.
                    return carrinhoRepository.save(newCarrinho);
                });

        return convertToDto(carrinho);
    }

    @Transactional
    public CarrinhoResponseDTO addItemToCarrinho(Long userId, ItemCarrinhoAddDTO itemDto) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário: " + userId));

        Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + itemDto.getProdutoId()));

        if (!produto.getDisponibilidade()) {
            throw new RuntimeException("Produto não disponível no momento: " + produto.getNome());
        }

        Optional<ItemCarrinho> existingItem = itemCarrinhoRepository.findByCarrinhoIdAndProdutoId(carrinho.getId(), produto.getId());

        ItemCarrinho itemCarrinho;
        if (existingItem.isPresent()) {
            itemCarrinho = existingItem.get();
            itemCarrinho.setQuantidade(itemCarrinho.getQuantidade() + itemDto.getQuantidade());
        } else {
            itemCarrinho = new ItemCarrinho();
            itemCarrinho.setCarrinho(carrinho);
            itemCarrinho.setProduto(produto);
            itemCarrinho.setQuantidade(itemDto.getQuantidade());
            itemCarrinho.setPrecoUnitario(produto.getPreco());
        }

        if (itemCarrinho.getQuantidade() <= 0) {
            itemCarrinhoRepository.delete(itemCarrinho);
        } else {
            itemCarrinhoRepository.save(itemCarrinho);
        }

        carrinho.setDataAtualizacao(LocalDateTime.now());
        // Não é necessário setar valorTotal na ENTIDADE aqui, pois ele foi removido dela.
        carrinhoRepository.save(carrinho);

        return getCarrinho(userId);
    }

    @Transactional
    public CarrinhoResponseDTO updateItemQuantity(Long userId, Long itemId, ItemCarrinhoUpdateDTO updateDto) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário: " + userId));

        ItemCarrinho itemCarrinho = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item do carrinho não encontrado com ID: " + itemId));

        if (!itemCarrinho.getCarrinho().getId().equals(carrinho.getId())) {
            throw new RuntimeException("Item do carrinho não pertence ao carrinho do usuário.");
        }

        itemCarrinho.setQuantidade(updateDto.getQuantidade());

        if (itemCarrinho.getQuantidade() <= 0) {
            itemCarrinhoRepository.delete(itemCarrinho);
        } else {
            itemCarrinhoRepository.save(itemCarrinho);
        }

        carrinho.setDataAtualizacao(LocalDateTime.now());
        // Não é necessário setar valorTotal na ENTIDADE aqui, pois ele foi removido dela.
        carrinhoRepository.save(carrinho);

        return getCarrinho(userId);
    }

    @Transactional
    public CarrinhoResponseDTO removeItemFromCarrinho(Long userId, Long itemId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário: " + userId));

        ItemCarrinho itemCarrinho = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item do carrinho não encontrado com ID: " + itemId));

        if (!itemCarrinho.getCarrinho().getId().equals(carrinho.getId())) {
            throw new RuntimeException("Item do carrinho não pertence ao carrinho do usuário.");
        }

        itemCarrinhoRepository.delete(itemCarrinho);
        carrinho.setDataAtualizacao(LocalDateTime.now());
        // Não é necessário setar valorTotal na ENTIDADE aqui, pois ele foi removido dela.
        carrinhoRepository.save(carrinho);

        return getCarrinho(userId);
    }

    @Transactional
    public void clearCarrinho(Long userId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário: " + userId));

        // Remove todos os itens associados a este carrinho
        itemCarrinhoRepository.deleteAll(carrinho.getItens());

        // Atualiza a lista de itens no carrinho (para refletir a remoção)
        carrinho.getItens().clear();
        carrinho.setDataAtualizacao(LocalDateTime.now());
        // Não é necessário setar valorTotal na ENTIDADE aqui, pois ele foi removido dela.
        carrinhoRepository.save(carrinho);
    }

    // Obter o carrinho de um usuário, calculando o total
    public CarrinhoResponseDTO getCarrinho(Long userId) {
        Carrinho carrinho = carrinhoRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário: " + userId));

        return convertToDto(carrinho);
    }

    // Método auxiliar para converter Entidade para DTO (Carrinho e seus itens)
    private CarrinhoResponseDTO convertToDto(Carrinho carrinho) {
        CarrinhoResponseDTO carrinhoDTO = new CarrinhoResponseDTO();
        carrinhoDTO.setId(carrinho.getId());
        carrinhoDTO.setUserId(carrinho.getUsuario().getId());
        carrinhoDTO.setUserName(carrinho.getUsuario().getName()); // Popula o nome do usuário no DTO

        List<ItemCarrinhoResponseDTO> itemDTOs = carrinho.getItens().stream()
                .map(item -> {
                    ItemCarrinhoResponseDTO itemCarrinhoDTO = new ItemCarrinhoResponseDTO();
                    itemCarrinhoDTO.setId(item.getId());
                    itemCarrinhoDTO.setProdutoId(item.getProduto().getId());
                    itemCarrinhoDTO.setNomeProduto(item.getProduto().getNome());
                    itemCarrinhoDTO.setImagemProduto(item.getProduto().getImagem());
                    itemCarrinhoDTO.setQuantidade(item.getQuantidade());
                    itemCarrinhoDTO.setPrecoUnitario(item.getPrecoUnitario());
                    // Calcula o subtotal para cada item.
                    itemCarrinhoDTO.setSubtotal(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
                    return itemCarrinhoDTO;
                })
                .collect(Collectors.toList());
        carrinhoDTO.setItens(itemDTOs);

        // Calcula o valor total do carrinho AGORA, no DTO, somando os subtotais dos itens.
        BigDecimal valorTotal = itemDTOs.stream()
                .map(ItemCarrinhoResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        carrinhoDTO.setValorTotal(valorTotal); // Define o valor total no DTO.

        return carrinhoDTO;
    }
}