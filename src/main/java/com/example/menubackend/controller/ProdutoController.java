package com.example.menubackend.controller;

import com.example.menubackend.model.Produto;
import com.example.menubackend.repository.ProdutoRepository;
import com.example.menubackend.dto.ProdutoDTO; 
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }


    @GetMapping 
    public ResponseEntity<List<ProdutoDTO>> findAll() {
        List<Produto> produtos = produtoRepository.findAll();
        List<ProdutoDTO> produtoDTOs = produtos.stream()
                .map(this::convertToProdutoDTO) 
                .collect(Collectors.toList());
        return ResponseEntity.ok(produtoDTOs);
    }

    @GetMapping("/{id}") 
    public ResponseEntity<ProdutoDTO> findById(@PathVariable long id) {
        return produtoRepository.findById(id)
                .map(this::convertToProdutoDTO) 
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }



    @PostMapping 
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoDTO> save(@Valid @RequestBody ProdutoDTO produtoDTO) {
 
        if (produtoDTO.getId() != null) {
            return ResponseEntity.badRequest().body(null); 
        }
        Produto produto = convertToProdutoEntity(produtoDTO); 
        Produto savedProduto = produtoRepository.save(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToProdutoDTO(savedProduto));
    }

    @PutMapping("/{id}") 
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<ProdutoDTO> update(@PathVariable Long id, @Valid @RequestBody ProdutoDTO produtoDTO) {
    
        if (produtoDTO.getId() == null || !produtoDTO.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        return produtoRepository.findById(id)
                .map(produtoExistente -> {
                    produtoExistente.setNome(produtoDTO.getNome());
                    produtoExistente.setDescricao(produtoDTO.getDescricao());
                    produtoExistente.setPreco(produtoDTO.getPreco());
                    produtoExistente.setCategoria(produtoDTO.getCategoria());
                    produtoExistente.setDisponibilidade(produtoDTO.getDisponibilidade());
                    produtoExistente.setImagem(produtoDTO.getImagem());

                    Produto updatedProduto = produtoRepository.save(produtoExistente);
                    return ResponseEntity.ok(convertToProdutoDTO(updatedProduto));
                })
                .orElse(ResponseEntity.notFound().build()); 
    }

    @DeleteMapping("/{id}") 
    @PreAuthorize("hasRole('ADMIN')") 
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        produtoRepository.deleteById(id);
    }

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

    private Produto convertToProdutoEntity(ProdutoDTO dto) {
        Produto produto = new Produto();
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
