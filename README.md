# 🚀 Spring Boot JWT API

## 📌 Sumário
- [Visão geral](#-visão-geral)
- [Tecnologias utilizadas](#-tecnologias-utilizadas)
- [Estrutura de diretórios](#-estrutura-de-diretórios)
- [Como executar](#-como-executar)
- [Console H2](#-console-h2)
- [Segurança – JWT](#-segurança--jwt)
- [Modelos de dados](#-modelos-de-dados)
- [DTOs](#-dtos-data-transfer-objects)
- [Endpoints da API](#-endpoints-da-api)
- [Camadas da aplicação](#-camadas-da-aplicação)
- [Como estender](#-como-estender)
- [Referências](#-referências)

---

## 📖 Visão geral

Este repositório contém um exemplo completo de **API RESTful** desenvolvida com **Java 17** e **Spring Boot 3**.

A aplicação demonstra:

- Modelagem de entidades com PK e FK  
- Uso de DTOs para entrada e saída de dados  
- Camada de serviço, repositório e tratamento de exceções  
- Autenticação baseada em JWT  
- Banco H2 em memória (ideal para testes e aprendizado)  

---

## 🛠 Tecnologias utilizadas

| Tecnologia | Versão |
|------------|--------|
| Java | 17 |
| Spring Boot | 3.2.2 |
| Spring Web | — |
| Spring Data JPA | — |
| Spring Security | — |
| H2 Database | — |
| JJWT (io.jsonwebtoken) | 0.11.5 |
| Lombok (opcional) | — |
| Maven | 3.8+ |
| Validation (jakarta.validation) | — |

---

## 📂 Estrutura de diretórios


src/
├─ main/
│ ├─ java/com/example/api/
│ │ ├─ ApiApplication.java
│ │ ├─ config/
│ │ │ ├─ SecurityConfig.java
│ │ │ ├─ JwtUtils.java
│ │ │ └─ JwtAuthenticationFilter.java
│ │ ├─ controller/
│ │ │ ├─ AuthController.java
│ │ │ └─ PostController.java
│ │ ├─ dto/
│ │ │ ├─ AuthRequest.java
│ │ │ ├─ AuthResponse.java
│ │ │ ├─ PostRequest.java
│ │ │ └─ PostResponse.java
│ │ ├─ entity/
│ │ │ ├─ User.java
│ │ │ ├─ Role.java
│ │ │ └─ Post.java
│ │ ├─ repository/
│ │ │ ├─ UserRepository.java
│ │ │ ├─ RoleRepository.java
│ │ │ └─ PostRepository.java
│ │ ├─ service/
│ │ │ ├─ UserService.java
│ │ │ └─ PostService.java
│ │ └─ exception/
│ │ └─ ResourceNotFoundException.java
│ └─ resources/
│ ├─ application.yml
│ └─ data.sql
pom.xml


---

## ▶ Como executar

### ✅ Pré-requisitos

- Java 17+
- Maven 3.8+

### 🔧 Passos

```bash
# 1. Clonar o repositório
git clone https://github.com/SEU_USUARIO/spring-boot-jwt-api.git
cd spring-boot-jwt-api

# 2. Compilar o projeto
mvn clean install

# 3. Iniciar a aplicação
mvn spring-boot:run

A aplicação será iniciada em:

http://localhost:8080
🗄 Console H2
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:demo-db
Usuário: sa
Senha: (vazia)
🔐 Segurança – JWT
🔄 Fluxo de autenticação

Registro
POST /api/auth/register

Login
POST /api/auth/login
Retorna um JWT no campo token.

Uso do token nas rotas protegidas:

Authorization: Bearer <seu_token_jwt>

O JwtAuthenticationFilter valida assinatura, validade e popula o SecurityContext.

⚠ Em produção, substitua a jwt.secret do application.yml por variável de ambiente.

🧩 Modelos de dados
👤 User
Campo	Tipo	Restrições
id	Long	PK
username	String	único, não nulo
password	String	hash BCrypt
roles	Set<Role>	@ManyToMany
🎭 Role
Campo	Tipo	Restrições
id	Long	PK
name	String	único (ex: ROLE_USER)
📝 Post
Campo	Tipo	Restrições
id	Long	PK
title	String	não nulo
content	String	—
createdAt	LocalDateTime	automático
author	User	@ManyToOne (FK user_id)
📦 DTOs (Data Transfer Objects)
DTO	Propósito	Campos	Validação
AuthRequest	Receber credenciais	username, password	@NotBlank
AuthResponse	Retornar token	token	—
PostRequest	Criar/atualizar post	title, content	@NotBlank
PostResponse	Resposta de leitura	id, title, content, createdAt, authorUsername	—
🌐 Endpoints da API
Método	URL	Auth	Descrição
POST	/api/auth/register	❌	Criar usuário
POST	/api/auth/login	❌	Retorna JWT
POST	/api/posts	✅	Criar post
GET	/api/posts	✅	Listar posts
GET	/api/posts/author/{authorId}	✅	Posts por autor
PUT	/api/posts/{postId}	✅	Atualizar post
DELETE	/api/posts/{postId}	✅	Remover post
GET	/h2-console/**	❌	Console H2
🧱 Camadas da aplicação

Controller
Expõe endpoints e converte DTOs.

Service
Contém regras de negócio e transações (@Transactional).

Repository
Interfaces que estendem JpaRepository.

Security
Configura rotas públicas e valida JWT.

Exception Handling
ResourceNotFoundException retorna 404.

🚀 Como estender
Funcionalidade	Como fazer
MapStruct	Criar interfaces @Mapper
Refresh Token	Criar entidade RefreshToken
Paginação	Usar Pageable
Swagger	Adicionar springdoc-openapi
Docker	Criar Dockerfile
Testes	@SpringBootTest e MockMvc
Rate Limiting	Integrar Bucket4j
📚 Referências

https://docs.spring.io/spring-boot/docs/3.2.2/reference/html/

https://spring.io/guides/tutorials/spring-boot-oauth2/

https://github.com/jwtk/jjwt

https://www.h2database.com/html/main.html

https://projectlombok.org/