Spring‑Boot REST FULL API com JWT, H2 e DTOs
Projeto: spring-boot-jwt-api
Tecnologias: Java 17, Spring Boot 3, Spring Security, Spring Data JPA, H2 (memória), JWT (jjwt), Lombok, Validation, Maven.
Objetivo: Demonstrar, passo a passo, como montar uma API REST FULL pronta para produção – com modelagem de entidades (PK/FK), DTOs, camada de serviço, controle de exceções, autenticação JWT e o banco em memória H2.

Estrutura de diretórios
spring-boot-jwt-api/
├─ src/
│   ├─ main/
│   │   ├─ java/com/example/api/
│   │   │   ├─ ApiApplication.java                # classe main
│   │   │
│   │   ├─ config/
│   │   │   ├─ SecurityConfig.java                # Spring Security
│   │   │   ├─ JwtUtils.java                     # geração/validação de token
│   │   │   └─ JwtAuthenticationFilter.java
│   │   │
│   │   ├─ controller/
│   │   │   ├─ AuthController.java               # registro e login
│   │   │   └─ PostController.java               # CRUD de posts (protected)
│   │   │
│   │   ├─ dto/
│   │   │   ├─ AuthRequest.java
│   │   │   ├─ AuthResponse.java
│   │   │   ├─ PostRequest.java
│   │   │   └─ PostResponse.java
│   │   │
│   │   ├─ entity/
│   │   │   ├─ User.java
│   │   │   ├─ Role.java
│   │   │   └─ Post.java
│   │   │
│   │   ├─ repository/
│   │   │   ├─ UserRepository.java
│   │   │   ├─ RoleRepository.java
│   │   │   └─ PostRepository.java
│   │   │
│   │   ├─ service/
│   │   │   ├─ UserService.java
│   │   │   └─ PostService.java
│   │   │
│   │   └─ exception/
│   │       └─ ResourceNotFoundException.java
│   │
│   └─ resources/
│       ├─ application.yml                      # propriedades
│       └─ data.sql                             # (opcional) dados iniciais
└─ pom.xml
Como rodar a aplicação
Pré‑requisitos
Java 17 (ou superior)
Maven 3.8+
IDE de sua preferência (IntelliJ, VS Code, Eclipse…)
Passos
# 1 – Clone o repositório (ou crie a pasta e cole os arquivos)
git clone https://github.com/SEU_USUARIO/spring-boot-jwt-api.git
cd spring-boot-jwt-api

# 2 – Compile e baixe as dependências
mvn clean install

# 3 – Rode a aplicação
mvn spring-boot:run
A API ficará disponível em http://localhost:8080.

H2 console
Acesse o console em: http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:demo-db
User: sa (senha vazia)
Segurança – JWT
Registro: POST /api/auth/register (público) – cria usuário com papel ROLE_USER.
Login: POST /api/auth/login (público) – valida credenciais e devolve um JWT.
O token deve ser enviado nos endpoints protegidos no cabeçalho:

Authorization: Bearer <seu_token_jwt>
O filtro JwtAuthenticationFilter intercepta as requisições, valida o token e popula o SecurityContext com o usuário autenticado.

Importante – A secret usada para assinar o JWT está em application.yml (jwt.secret). Em produção, carregue‑a a partir de variáveis de ambiente ou Vault.

Modelos de dados (Entidades)
Entidade	Campos	Relações	Comentário
User	id (PK), username (unique), password (BCrypt), roles (Set)	@ManyToMany com Role (tabela users_roles)	Usuário da aplicação.
Role	id (PK), name (ex.: ROLE_USER)	—	Papéis/authorities.
Post	id (PK), title, content, createdAt, author (User)	@ManyToOne → User	Exemplo de recurso que pertence a um usuário.
FK / PK
PKs são gerados automaticamente (@GeneratedValue(strategy = GenerationType.IDENTITY)).
FK de Post.author_id aponta para User.id.
A tabela de junção users_roles contém duas FK (user_id, role_id).
DTOs (Data Transfer Objects)
DTOs são usados para não expor diretamente as entidades JPA nas respostas e para validar a entrada do cliente.

DTO	Uso	Campos	Validações
AuthRequest	Login / Registro	username, password	@NotBlank
AuthResponse	Resposta de login	token	—
PostRequest	Criar / Atualizar post	title, content	@NotBlank
PostResponse	Resposta de leitura de post	id, title, content, createdAt, authorUsername	—
 Endpoints da API
Método	URL	Autenticação	Body (JSON)	Descrição
POST	/api/auth/register	❌ (público)	{ "username":"alice","password":"pwd123" }	Cria novo usuário (ROLE_USER).
POST	/api/auth/login	❌ (público)	{ "username":"alice","password":"pwd123" }	Retorna JWT (AuthResponse).
POST	/api/posts	✅ (Bearer)	{ "title":"Meu post","content":"Olá!" }	Cria um post associado ao usuário autenticado.
GET	/api/posts	✅ (Bearer) – mas pode ser aberto se desejar	—	Lista todos os posts.
GET	/api/posts/author/{authorId}	✅ (Bearer)	—	Lista posts de um autor específico.
PUT	/api/posts/{postId}	✅ (Bearer)	{ "title":"Novo título","content":"Texto alterado" }	Atualiza post – apenas o autor pode.
DELETE	/api/posts/{postId}	✅ (Bearer)	—	Remove post – apenas o autor pode.
GET	/h2-console/**	❌ (público)	—	Console web do H2 (útil para dev).
 Dica: Use a ferramenta Postman, Insomnia ou curl para testar.

Exemplo de login com curl
curl -X POST http://localhost:8080/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{"username":"alice","password":"pwd123"}'
Resposta:

{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
Exemplo de criação de post com o token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{"username":"alice","password":"pwd123"}' | jq -r .token)

curl -X POST http://localhost:8080/api/posts \
   -H "Content-Type: application/json" \
   -H "Authorization: Bearer $TOKEN" \
   -d '{"title":"Primeiro post","content":"Hello world"}'
 Camadas da aplicação
Controller – recebe as requisições, converte DTOs e delega ao service.
Service – contém a lógica de negócios, transações (@Transactional) e validações de domínio.
Repository – extends JpaRepository, fornece CRUD e consultas personalizadas.
Security – filtro JWT + SecurityConfig configuram a proteção de rotas e gerenciam o AuthenticationManager.
Exception handling – ResourceNotFoundException devolve 404; demais exceções são tratadas automaticamente pelo Spring (ou podem ser customizadas com @ControllerAdvice).
🛠️ Como customizar / evoluir
Funcionalidade	Como adicionar
MapStruct (mapper DTO ↔ Entity)	xml <dependency> <groupId>org.mapstruct</groupId> <artifactId>mapstruct</artifactId> <version>1.5.5.Final</version> </dependency> e criar interfaces anotadas com @Mapper.
Refresh Token	Crie entidade RefreshToken com user, expiryDate; adicione endpoint /api/auth/refresh.
Paginação	Nos repositórios use Page<Post> findAll(Pageable pageable); e nos controllers receba Pageable como parâmetro.
OpenAPI/Swagger	Dependência springdoc-openapi-starter-webmvc-ui; a UI fica em /swagger-ui.html.
Docker	Dockerfile com FROM eclipse-temurin:17-jdk, COPY target/*.jar app.jar, ENTRYPOINT ["java","-jar","/app.jar"].
Testes	@SpringBootTest + MockMvc para testes de integração; @DataJpaTest para repositórios.
Rate Limiting	Biblioteca Bucket4j ou resilience4j e um filtro que verifica os limites por IP/chave.
Referências úteis
Tema	Link
Spring Boot 3 Docs	https://docs.spring.io/spring-boot/docs/3.2.2/reference/html/
Spring Security – JWT	https://spring.io/guides/tutorials/spring-boot-oauth2/
JJWT (jjwt)	https://github.com/jwtk/jjwt
Lombok	https://projectlombok.org/
H2 Database	https://www.h2database.com/html/main.html
Spring Data JPA	https://spring.io/projects/spring-data-jpa