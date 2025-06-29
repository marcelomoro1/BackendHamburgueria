package com.example.menubackend.controller;

import com.example.menubackend.model.Produto;
import com.example.menubackend.repository.ProdutoRepository;
import com.example.menubackend.dto.ProdutoDTO; // Usando o SEU ProdutoDTO existente

import jakarta.validation.Valid; // Para validação dos DTOs

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Para proteger endpoints
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors; // Para mapear listas

@RestController
@RequestMapping("/api/produtos") // Boas práticas: prefixar com /api
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem (em desenvolvimento)
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // --- Endpoints Públicos ou Acessíveis a Todos (CLIENTE e ADMIN) ---

    @GetMapping // Lista todos os produtos
    public ResponseEntity<List<ProdutoDTO>> findAll() {
        List<Produto> produtos = produtoRepository.findAll();
        List<ProdutoDTO> produtoDTOs = produtos.stream()
                .map(this::convertToProdutoDTO) // Converte Entidade para SEU ProdutoDTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtoDTOs);
    }

    @GetMapping("/{id}") // Busca produto por ID
    public ResponseEntity<ProdutoDTO> findById(@PathVariable long id) {
        return produtoRepository.findById(id)
                .map(this::convertToProdutoDTO) // Converte Entidade para SEU ProdutoDTO
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrar
    }

    // --- Endpoints Protegidos (Apenas para ADMIN) ---

    @PostMapping // Cria um novo produto (apenas ADMIN)
    @PreAuthorize("hasRole('ADMIN')") // Exige ROLE_ADMIN
    public ResponseEntity<ProdutoDTO> save(@Valid @RequestBody ProdutoDTO produtoDTO) {
        // ID deve ser nulo para criação, será gerado pelo banco de dados
        if (produtoDTO.getId() != null) {
            return ResponseEntity.badRequest().body(null); // Não deve enviar ID na criação
        }
        Produto produto = convertToProdutoEntity(produtoDTO); // Converte SEU ProdutoDTO para Entidade
        Produto savedProduto = produtoRepository.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToProdutoDTO(savedProduto));
    }

    @PutMapping("/{id}") // Atualiza um produto existente (apenas ADMIN)
    @PreAuthorize("hasRole('ADMIN')") // Exige ROLE_ADMIN
    public ResponseEntity<ProdutoDTO> update(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {
        // Garante que o ID do DTO corresponda ao ID do PathVariable
        if (produtoDTO.getId() == null || !produtoDTO.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        return produtoRepository.findById(id)
                .map(produtoExistente -> {
                    // Atualiza os campos do produto existente com os dados do DTO
                    produtoExistente.setNome(produtoDTO.getNome());
                    produtoExistente.setDescricao(produtoDTO.getDescricao());
                    produtoExistente.setPreco(produtoDTO.getPreco());
                    produtoExistente.setCategoria(produtoDTO.getCategoria());
                    produtoExistente.setDisponibilidade(produtoDTO.getDisponibilidade());
                    produtoExistente.setImagem(produtoDTO.getImagem());

                    Produto updatedProduto = produtoRepository.save(produtoExistente);
                    return ResponseEntity.ok(convertToProdutoDTO(updatedProduto));
                })
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se não encontrar
    }

    @DeleteMapping("/{id}") // Deleta um produto (apenas ADMIN)
    @PreAuthorize("hasRole('ADMIN')") // Exige ROLE_ADMIN
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna 204 No Content para exclusão bem-sucedida
    public void delete(@PathVariable Long id) {
        produtoRepository.deleteById(id);
    }

    // --- Métodos de Conversão (DTO <-> Entity) ---
    // Converte de Entidade Produto para SEU ProdutoDTO
    private ProdutoDTO convertToProdutoDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPreco(produto.getPreco());
        dto.setCategoria(produto.getCategoria());
        dto.setDisponibilidade(produto.getDisponibilidade());
        dto.setImagem(produto.getImagem());
        return dto;
    }

    // Converte de SEU ProdutoDTO para Entidade Produto
    private Produto convertToProdutoEntity(ProdutoDTO dto) {
        Produto produto = new Produto();
        // ID é definido apenas para operações de atualização ou quando vem do banco
        // Para criação, não defina o ID, pois ele é autogerado
        if (dto.getId() != null) {
            produto.setId(dto.getId());
        }
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
        produto.setDisponibilidade(dto.getDisponibilidade());
        produto.setImagem(dto.getImagem());
        return produto;
    }
}