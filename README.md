# 🍔 Backend Hamburgueria

API RESTful para gerenciamento de pedidos, produtos, usuários e carrinho de compras para sistemas de delivery, restaurantes ou lanchonetes. Desenvolvido com Spring Boot 3, Java 17, autenticação JWT, MySQL e boas práticas modernas.

---

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Security (JWT)
- MySQL
- Lombok
- Maven

---

## ⚙️ Instalação e Execução

### Pré-requisitos

- Java 17+
- Maven 3.8+
- MySQL 5.7+ ou 8.x

### Configuração do Banco de Dados

No arquivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/menu?createDatabaseIfNotExist=true
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

> O banco será criado automaticamente ao rodar a aplicação.

### Rodando o Projeto

```bash
# Clone o repositório
git clone https://github.com/marcelomoro1/BackendHamburgueria.git
cd BackendHamburgueria

# Compile e rode os testes
./mvnw clean install

# Rode a aplicação
./mvnw spring-boot:run
```

A API estará disponível em: [http://localhost:8080](http://localhost:8080)

---

## 🔐 Autenticação

- **JWT Token**: Após login, inclua o token no header `Authorization: Bearer <token>` para acessar rotas protegidas.
- **Roles**: ADMIN e CLIENTE.

---

## 🗂️ Principais Endpoints

### Auth

| Método | Endpoint         | Descrição                |
|--------|------------------|--------------------------|
| POST   | /api/auth/signin | Login (retorna JWT)      |
| POST   | /api/auth/signup | Registro de usuário      |

### Produtos

| Método | Endpoint           | Descrição                | Auth      |
|--------|--------------------|--------------------------|-----------|
| GET    | /api/produtos      | Listar produtos          | Público   |
| GET    | /api/produtos/{id} | Detalhe do produto       | Público   |
| POST   | /api/produtos      | Criar produto            | ADMIN     |
| PUT    | /api/produtos/{id} | Atualizar produto        | ADMIN     |
| DELETE | /api/produtos/{id} | Remover produto          | ADMIN     |

### Carrinho

| Método | Endpoint                        | Descrição                        | Auth                |
|--------|---------------------------------|----------------------------------|---------------------|
| GET    | /api/carrinho/meu               | Ver carrinho                     | CLIENTE/ADMIN       |
| POST   | /api/carrinho/adicionar         | Adicionar item                   | CLIENTE/ADMIN       |
| PUT    | /api/carrinho/atualizar/{itemId}| Atualizar quantidade de item     | CLIENTE/ADMIN       |
| DELETE | /api/carrinho/remover/{itemId}  | Remover item                     | CLIENTE/ADMIN       |
| DELETE | /api/carrinho/limpar            | Limpar carrinho                  | CLIENTE/ADMIN       |

### Pedidos

| Método | Endpoint                        | Descrição                        | Auth                |
|--------|---------------------------------|----------------------------------|---------------------|
| GET    | /api/pedidos                    | Listar todos os pedidos          | ADMIN               |
| POST   | /api/pedidos/finalizar          | Finalizar pedido (do carrinho)   | CLIENTE             |
| GET    | /api/pedidos/meus               | Listar meus pedidos              | CLIENTE/ADMIN       |
| GET    | /api/pedidos/{id}               | Detalhe do pedido                | CLIENTE/ADMIN       |
| PUT    | /api/pedidos/admin/{id}/status?newStatus=STATUS | Atualizar status do pedido | ADMIN               |

---

## 📦 Estrutura dos Principais DTOs

### Exemplo: ProdutoDTO

```json
{
  "id": 1,
  "nome": "X-Burger",
  "descricao": "Hambúrguer artesanal",
  "preco": 25.00,
  "categoria": "LANCHE",
  "disponibilidade": true,
  "imagem": "https://..."
}
```

### Exemplo: PedidoResponseDTO

```json
{
  "id": 10,
  "userId": 2,
  "userName": "João",
  "dataPedido": "2024-06-23T01:09:59.398",
  "status": "PENDENTE",
  "valorTotal": 50.00,
  "itens": [
    {
      "id": 1,
      "produtoId": 1,
      "nomeProduto": "X-Burger",
      "imagemProduto": "https://...",
      "quantidade": 2,
      "precoUnitario": 25.00,
      "subtotal": 50.00
    }
  ]
}
```

---

## 🛡️ Segurança

- **Autenticação JWT**: Protege rotas sensíveis.
- **Roles**: Controle de acesso por perfil (ADMIN, CLIENTE).
- **Validação**: Todos os DTOs possuem validação de campos.

---

## 🏗️ Arquitetura

- **Camadas**: Controller → Service → Repository → Model
- **DTOs**: Separação clara entre entidades e dados expostos na API.
- **Spring Security**: Implementação robusta de autenticação e autorização.
- **Lombok**: Redução de boilerplate.

---

## 📝 Exemplos de Uso

### 1. Registro

```http
POST /api/auth/signup
Content-Type: application/json

{
  "nome": "Maria",
  "email": "maria@email.com",
  "password": "123456"
}
```

### 2. Login

```http
POST /api/auth/signin
Content-Type: application/json

{
  "email": "maria@email.com",
  "password": "123456"
}
```

**Resposta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### 3. Adicionar item ao carrinho

```http
POST /api/carrinho/adicionar
Authorization: Bearer <token>
Content-Type: application/json

{
  "produtoId": 1,
  "quantidade": 2
}
```

---

## 🧑‍💻 Contribuição

Pull requests são bem-vindos! Sinta-se à vontade para abrir issues e sugerir melhorias.

---

## 📄 Licença

Este projeto está sob a licença MIT.

---

## 📚 Referências

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [JWT.io](https://jwt.io/)
- [Lombok](https://projectlombok.org/)

---

> Dúvidas? Abra uma issue ou entre em contato!
