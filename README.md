Spring‑Boot JWT API
Sumário
Visão geral
Tecnologias utilizadas
Estrutura de diretórios
Como executar
Console H2
Segurança – JWT
Modelos de dados
DTOs (Data Transfer Objects)
Endpoints da API
Camadas da aplicação
Como estender
Referências
Commit padrão
Visão geral
Este repositório contém um exemplo completo de API REST FULL desenvolvida com Java 17 e Spring Boot 3.
A aplicação demonstra:

Modelagem de entidades com chaves primárias (PK) e estrangeiras (FK).
Uso de DTOs para entrada e saída de dados, com validação.
Camada de serviço, repositório e tratamento de exceções.
Autenticação baseada em JSON Web Token (JWT).
Banco de dados H2 em memória (ideal para testes e aprendizado).
Tecnologias utilizadas
Tecnologia	Versão
Java	17
Spring Boot	3.2.2
Spring Web	—
Spring Data JPA	—
Spring Security	—
H2 Database	—
JJWT (io.jsonwebtoken)	0.11.5
Lombok (opcional)	—
Maven	3.8+
Validation (jakarta.validation)	—
Estrutura de diretórios
src/
├─ main/
│   ├─ java/com/example/api/
│   │   ├─ ApiApplication.java
│   │   ├─ config/
│   │   │   ├─ SecurityConfig.java
│   │   │   ├─ JwtUtils.java
│   │   │   └─ JwtAuthenticationFilter.java
│   │   ├─ controller/
│   │   │   ├─ AuthController.java
│   │   │   └─ PostController.java
│   │   ├─ dto/
│   │   │   ├─ AuthRequest.java
│   │   │   ├─ AuthResponse.java
│   │   │   ├─ PostRequest.java
│   │   │   └─ PostResponse.java
│   │   ├─ entity/
│   │   │   ├─ User.java
│   │   │   ├─ Role.java
│   │   │   └─ Post.java
│   │   ├─ repository/
│   │   │   ├─ UserRepository.java
│   │   │   ├─ RoleRepository.java
│   │   │   └─ PostRepository.java
│   │   ├─ service/
│   │   │   ├─ UserService.java
│   │   │   └─ PostService.java
│   │   └─ exception/
│   │       └─ ResourceNotFoundException.java
│   └─ resources/
│       ├─ application.yml
│       └─ data.sql  (opcional)
pom.xml
Como executar
Pré‑requisitos
Java 17 (ou superior) instalado e configurado no PATH.
Maven 3.8+ disponível.
Passos
# 1. Clonar o repositório
git clone https://github.com/SEU_USUARIO/spring-boot-jwt-api.git
cd spring-boot-jwt-api

# 2. Compilar e baixar as dependências
mvn clean install

# 3. Iniciar a aplicação
mvn spring-boot:run
A API será iniciada em http://localhost:8080.

Console H2
A aplicação inclui o console web do H2 para inspeção direta das tabelas.

URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:demo-db
Usuário: sa (senha vazia)
Segurança – JWT
Fluxo de autenticação
Registro – POST /api/auth/register (campo username e password).
Login – POST /api/auth/login. Se as credenciais forem válidas, o endpoint devolve um JWT no campo token.
Uso do token – Em todas as chamadas protegidas (ex.: /api/posts) inclua o cabeçalho
Authorization: Bearer <seu_token_jwt>
O filtro JwtAuthenticationFilter verifica assinatura, validade e extrai o usuário, populando o SecurityContext para que o Spring Security autorize o acesso.

Observação – A chave secreta usada para assinar o token está em application.yml (jwt.secret). Em ambientes de produção substitua‑a por uma variável de ambiente ou serviço de segredo.

Modelos de dados
User
Campo	Tipo	Restrições
id	Long (PK)	gerado automaticamente
username	String	único, não nulo
password	String	hash BCrypt
roles	Set	relacionamento @ManyToMany (tabela users_roles)
Role
Campo	Tipo	Restrições
id	Long (PK)	gerado
name	String	único (ex.: ROLE_USER)
Post
Campo	Tipo	Restrições
id	Long (PK)	gerado
title	String	não nulo
content	String	–
createdAt	LocalDateTime	preenchido ao persistir
author	User	FK (user_id) – relacionamento @ManyToOne
DTOs (Data Transfer Objects)
DTO	Propósito	Campos	Validação
AuthRequest	Receber credenciais	username, password	@NotBlank
AuthResponse	Devolver token	token	–
PostRequest	Criar ou atualizar post	title, content	@NotBlank
PostResponse	Resposta de leitura	id, title, content, createdAt, authorUsername	–
Endpoints da API
Método	URL	Autenticação	Corpo (JSON)	Descrição
POST	/api/auth/register	não	{ "username":"alice","password":"pwd123" }	Cria usuário com papel ROLE_USER.
POST	/api/auth/login	não	{ "username":"alice","password":"pwd123" }	Retorna JWT (AuthResponse).
POST	/api/posts	sim (Bearer)	{ "title":"Meu post","content":"Olá mundo!" }	Cria post associado ao usuário autenticado.
GET	/api/posts	sim (Bearer)	–	Lista todos os posts.
GET	/api/posts/author/{authorId}	sim (Bearer)	–	Lista posts de um autor.
PUT	/api/posts/{postId}	sim (Bearer)	{ "title":"Novo título","content":"Texto alterado" }	Atualiza post; apenas o autor pode.
DELETE	/api/posts/{postId}	sim (Bearer)	–	Remove post; apenas o autor pode.
GET	/h2-console/**	não	–	Acesso ao console H2 (para desenvolvimento).
Exemplo de login via curl
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"alice","password":"pwd123"}'
Exemplo de criação de post usando o token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"alice","password":"pwd123"}' | jq -r .token)

curl -X POST http://localhost:8080/api/posts \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{"title":"Primeiro post","content":"Hello world"}'
Camadas da aplicação
Controller – expõe os endpoints, converte DTOs e delega ao service.
Service – contém a lógica de negócio, transações (@Transactional) e validações de domínio.
Repository – interfaces que estendem JpaRepository, fornecendo CRUD e queries personalizadas.
Security – SecurityConfig estabelece quais rotas são públicas, JwtAuthenticationFilter valida o token e preenche o contexto de segurança.
Exception handling – ResourceNotFoundException gera resposta 404; outras exceções são tratadas pelo mecanismo padrão do Spring (ou podem ser customizadas com @ControllerAdvice).
Como estender
Funcionalidade	Passos resumidos
MapStruct (mapper DTO ↔ Entity)	Adicionar dependência org.mapstruct:mapstruct, criar interfaces anotadas com @Mapper e gerar implementações.
Refresh Token	Criar entidade RefreshToken com relação @OneToOne a User; expor endpoint /api/auth/refresh.
Paginação	Nos repositórios usar Page<T> findAll(Pageable pageable); e aceitar Pageable como parâmetro nos controllers.
Swagger / OpenAPI	Incluir dependência org.springdoc:springdoc-openapi-starter-webmvc-ui; UI disponível em /swagger-ui.html.
Docker	Criar Dockerfile com FROM eclipse-temurin:17-jdk, copiar .jar e definir ENTRYPOINT ["java","-jar","/app.jar"].
Testes	Utilizar @SpringBootTest + MockMvc para testes de integração e @DataJpaTest para repositórios.
Rate Limiting	Integrar Bucket4j ou resilience4j em um filtro que controla o número de requisições por IP/chave.
Referências
Spring Boot 3 Documentation – https://docs.spring.io/spring-boot/docs/3.2.2/reference/html/
Spring Security – JWT Guide – https://spring.io/guides/tutorials/spring-boot-oauth2/
JJWT (io.jsonwebtoken) – https://github.com/jwtk/jjwt
H2 Database – https://www.h2database.com/html/main.html
Lombok – https://projectlombok.org/
