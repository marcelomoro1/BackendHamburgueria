package com.example.menubackend.config;

import com.example.menubackend.model.Categoria; // NOVO IMPORT
import com.example.menubackend.model.Produto;
import com.example.menubackend.model.Role;
import com.example.menubackend.model.User;
import com.example.menubackend.repository.ProdutoRepository;
import com.example.menubackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initDatabase(ProdutoRepository produtoRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            logger.info("Verificando e inicializando dados de produtos e usuários...");

            // 1. Inicializar Usuários (Opcional, mas útil para testes)
            if (userRepository.count() == 0) {
                logger.info("Criando usuários iniciais...");

                // Usuário Cliente
                User cliente = new User("Cliente Teste", "cliente@email.com", "cliente@email.com", passwordEncoder.encode("cliente"));
                Set<Role> clienteRoles = new HashSet<>();
                clienteRoles.add(Role.CLIENTE);
                cliente.setRoles(clienteRoles);
                userRepository.save(cliente);
                logger.info("Usuário Cliente criado: {}", cliente.getEmail());

                // Usuário Admin
                User admin = new User("Admin Teste", "admin@email.com", "admin@email.com", passwordEncoder.encode("admin"));
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(Role.ADMIN);
                admin.setRoles(adminRoles);
                userRepository.save(admin);
                logger.info("Usuário Admin criado: {}", admin.getEmail());

            } else {
                logger.info("Usuários já existem, pulando inicialização de usuários.");
            }

            // 2. Inicializar Produtos
            if (produtoRepository.count() == 0) {
                logger.info("Adicionando produtos iniciais ao banco de dados...");

                Produto hamburguerMoroClassico = new Produto();
                hamburguerMoroClassico.setNome("Clássico");
                hamburguerMoroClassico.setDescricao("Hambúrguer de 180g, queijo cheddar, alface, tomate, picles e maionese da casa no pão brioche.");
                hamburguerMoroClassico.setPreco(new BigDecimal("35.00"));
                hamburguerMoroClassico.setCategoria(Categoria.LANCHE);
                hamburguerMoroClassico.setDisponibilidade(true);
                hamburguerMoroClassico.setImagem("https://minervafoods.com/wp-content/uploads/2022/12/burguer-de-picanha.jpg"); // Substitua com URL real
                produtoRepository.save(hamburguerMoroClassico);
                logger.info("Produto '{}' adicionado.", hamburguerMoroClassico.getNome());

                Produto hamburguerSmashDuplo = new Produto();
                hamburguerSmashDuplo.setNome("Smash Duplo Bacon");
                hamburguerSmashDuplo.setDescricao("Dois smash burgers de 100g, queijo prato, fatias de bacon crocante e molho barbecue especial.");
                hamburguerSmashDuplo.setPreco(new BigDecimal("38.50"));
                hamburguerSmashDuplo.setCategoria(Categoria.LANCHE);
                hamburguerSmashDuplo.setDisponibilidade(true);
                hamburguerSmashDuplo.setImagem("https://atm-accounts.s3-sa-east-1.amazonaws.com/156/files/thumbs/mz8ze911n76c1qxztd35-large.jpg"); // Substitua com URL real
                produtoRepository.save(hamburguerSmashDuplo);
                logger.info("Produto '{}' adicionado.", hamburguerSmashDuplo.getNome());

                Produto hamburguerVegetariano = new Produto();
                hamburguerVegetariano.setNome("Vegano");
                hamburguerVegetariano.setDescricao("Hambúrguer de grão de bico e legumes, queijo coalho grelhado, rúcula, tomate seco e maionese vegana.");
                hamburguerVegetariano.setPreco(new BigDecimal("32.00"));
                hamburguerVegetariano.setCategoria(Categoria.LANCHE);
                hamburguerVegetariano.setDisponibilidade(true);
                hamburguerVegetariano.setImagem("https://cdn.deliway.com.br/blog/base/619/e8c/451/hamburguer-vegano-soja.jpg"); // Substitua com URL real
                produtoRepository.save(hamburguerVegetariano);
                logger.info("Produto '{}' adicionado.", hamburguerVegetariano.getNome());

                Produto hamburguerPicante = new Produto();
                hamburguerPicante.setNome("Picante");
                hamburguerPicante.setDescricao("Hambúrguer de 200g, queijo pepper jack, jalapeños, pimentão vermelho, cebola roxa e molho sriracha.");
                hamburguerPicante.setPreco(new BigDecimal("42.00"));
                hamburguerPicante.setCategoria(Categoria.LANCHE);
                hamburguerPicante.setDisponibilidade(true);
                hamburguerPicante.setImagem("https://alloydeliveryimages.s3.sa-east-1.amazonaws.com/item_images/10114/65b6951018e8dosvle.webp"); // Substitua com URL real
                produtoRepository.save(hamburguerPicante);
                logger.info("Produto '{}' adicionado.", hamburguerPicante.getNome());

                Produto hamburguerKids = new Produto();
                hamburguerKids.setNome("Mini Burger Kids");
                hamburguerKids.setDescricao("Mini hambúrguer de carne, queijo e pão macio, acompanhado de batata frita pequena.");
                hamburguerKids.setPreco(new BigDecimal("25.00"));
                hamburguerKids.setCategoria(Categoria.LANCHE);
                hamburguerKids.setDisponibilidade(true);
                hamburguerKids.setImagem("https://cdn.outback.com.br/wp-data/wp-content/uploads/2024/07/OTB_Burger-Kids-copy-2-675x750.jpeg"); // Substitua com URL real
                produtoRepository.save(hamburguerKids);
                logger.info("Produto '{}' adicionado.", hamburguerKids.getNome());


                // --- SOBREMESA (1 tipo) ---
                Produto petitGateau = new Produto();
                petitGateau.setNome("Petit Gateau com Sorvete");
                petitGateau.setDescricao("Bolo de chocolate com centro cremoso, servido com uma bola de sorvete de creme.");
                petitGateau.setPreco(new BigDecimal("20.00"));
                petitGateau.setCategoria(Categoria.SOBREMESA);
                petitGateau.setDisponibilidade(true);
                petitGateau.setImagem("https://www.skimoni.com.br/wp-content/uploads/2020/05/petit_gateau.png"); // Substitua com URL real
                produtoRepository.save(petitGateau);
                logger.info("Produto '{}' adicionado.", petitGateau.getNome());

                // --- APERITIVO (1 tipo) ---
                Produto batataFritaCheddarBacon = new Produto();
                batataFritaCheddarBacon.setNome("Batata Frita com Cheddar e Bacon");
                batataFritaCheddarBacon.setDescricao("Porção generosa de batatas fritas crocantes cobertas com queijo cheddar cremoso e pedaços de bacon.");
                batataFritaCheddarBacon.setPreco(new BigDecimal("28.00"));
                batataFritaCheddarBacon.setCategoria(Categoria.APERITIVO);
                batataFritaCheddarBacon.setDisponibilidade(true);
                batataFritaCheddarBacon.setImagem("https://i.ytimg.com/vi/0Fea2vwfnN8/maxresdefault.jpg"); // Substitua com URL real
                produtoRepository.save(batataFritaCheddarBacon);
                logger.info("Produto '{}' adicionado.", batataFritaCheddarBacon.getNome());

                // --- BEBIDAS (2 tipos) ---
                Produto refrigeranteCola = new Produto();
                refrigeranteCola.setNome("Refrigerante Cola (Lata)");
                refrigeranteCola.setDescricao("Lata de 350ml de refrigerante sabor cola.");
                refrigeranteCola.setPreco(new BigDecimal("8.00"));
                refrigeranteCola.setCategoria(Categoria.BEBIDA);
                refrigeranteCola.setDisponibilidade(true);
                refrigeranteCola.setImagem("https://zaffari.vtexassets.com/arquivos/ids/276576/1007841-00.jpg?v=638802406334870000"); // Substitua com URL real
                produtoRepository.save(refrigeranteCola);
                logger.info("Produto '{}' adicionado.", refrigeranteCola.getNome());

                Produto aguaMineral = new Produto();
                aguaMineral.setNome("Água Mineral sem Gás");
                aguaMineral.setDescricao("Garrafa de 500ml de água mineral.");
                aguaMineral.setPreco(new BigDecimal("6.00"));
                aguaMineral.setCategoria(Categoria.BEBIDA);
                aguaMineral.setDisponibilidade(true);
                aguaMineral.setImagem("https://foodtrailer46.meucatalogofacil.com/_core/_uploads//2022/05/2214200522hiegigf0fi.jpeg"); // Substitua com URL real
                produtoRepository.save(aguaMineral);
                logger.info("Produto '{}' adicionado.", aguaMineral.getNome());

            } else {
                logger.info("Produtos já existem no banco de dados, pulando inicialização de produtos.");
            }
            logger.info("Inicialização de dados concluída.");
        };
    }
}