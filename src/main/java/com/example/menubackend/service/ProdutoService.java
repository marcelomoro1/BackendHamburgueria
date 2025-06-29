package com.example.menubackend.service;

import com.example.menubackend.dto.ProdutoDTO;
import com.example.menubackend.model.Produto;
import com.example.menubackend.model.Categoria; // Importar a enum Categoria
import com.example.menubackend.repository.ProdutoRepository;
import org.springframework.beans.BeanUtils; // Para copiar propriedades
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // Criar um novo produto
    public ProdutoDTO createProduto(ProdutoDTO produtoDTO) {
        Produto produto = new Produto();
        // Copia as propriedades do DTO para a entidade
        BeanUtils.copyProperties(produtoDTO, produto);
        // Garante que o ID não seja setado na criação
        produto.setId(null);
        // O status (disponibilidade) pode ser definido como ativo por padrão na criação, se desejar
        if (produto.getDisponibilidade() == null) {
            produto.setDisponibilidade(true);
        }

        Produto savedProduto = produtoRepository.save(produto);
        // Copia as propriedades da entidade salva de volta para um novo DTO para retorno
        ProdutoDTO responseDTO = new ProdutoDTO();
        BeanUtils.copyProperties(savedProduto, responseDTO);
        return responseDTO;
    }

    // Listar todos os produtos (para Admin)
    public List<ProdutoDTO> getAllProdutos() {
        return produtoRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Listar produtos ativos (para Cliente)
    public List<ProdutoDTO> getActiveProdutos() {
        return produtoRepository.findByDisponibilidadeTrue().stream() // Assumindo que você adicione este método no ProdutoRepository
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Buscar produto por ID
    public Optional<ProdutoDTO> getProdutoById(Long id) {
        return produtoRepository.findById(id)
                .map(this::convertToDto);
    }

    // Atualizar um produto existente
    public ProdutoDTO updateProduto(Long id, ProdutoDTO produtoDTO) {
        return produtoRepository.findById(id).map(existingProduto -> {
            // Copia as propriedades do DTO para a entidade existente, ignorando o ID
            BeanUtils.copyProperties(produtoDTO, existingProduto, "id");
            Produto updatedProduto = produtoRepository.save(existingProduto);
            return convertToDto(updatedProduto);
        }).orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
    }

    // Deletar um produto
    public void deleteProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado com ID: " + id);
        }
        produtoRepository.deleteById(id);
    }

    // Ativar ou desativar um produto
    public ProdutoDTO toggleProdutoDisponibilidade(Long id, boolean disponibilidade) {
        return produtoRepository.findById(id).map(produto -> {
            produto.setDisponibilidade(disponibilidade);
            Produto updatedProduto = produtoRepository.save(produto);
            return convertToDto(updatedProduto);
        }).orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
    }

    // Método auxiliar para converter Entidade para DTO
    private ProdutoDTO convertToDto(Produto produto) {
        ProdutoDTO produtoDTO = new ProdutoDTO();
        BeanUtils.copyProperties(produto, produtoDTO);
        return produtoDTO;
    }
}