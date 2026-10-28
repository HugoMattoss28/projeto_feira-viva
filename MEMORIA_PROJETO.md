# 🧠 Memória do Projeto — Feira Viva (Contexto Completo para IA)

> **Como usar este arquivo em casa:**  
> Ao abrir o projeto na sua IA em casa, basta enviar:  
> *"Leia o arquivo `MEMORIA_PROJETO.md` para ter todo o contexto da arquitetura, histórico das aulas e particularidades do projeto."*

---

## 1. Identificação e Stack Técnica

- **Projeto:** Feira Viva — Backend API (E-commerce de produtos locais)
- **Disciplina:** Desenvolvimento para Servidores II (5º Semestre / 2026 — Prof. James Campos)
- **Java:** 21 LTS
- **Spring Boot:** `4.1.1` (Jakarta EE 11 · Spring Framework 7 · Spring Security 7 · Hibernate 7.4.5)
- **Banco de Dados (Dev):** H2 em memória (`jdbc:h2:mem:feiraviva;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1`)
- **Versionamento de Banco:** Flyway (`spring-boot-starter-flyway` — Flyway 12.4)
- **Autenticação/Autorização:** Spring Security 7 + JWT HS256 (`io.jsonwebtoken:jjwt` `0.12.6`)
- **Documentação da API:** `springdoc-openapi-starter-webmvc-ui` `3.0.1` (`/swagger-ui.html` com botão **Authorize** Bearer JWT e `/api-docs`)
- **Pacote Base:** `br.com.feiraviva`
- **Porta padrão:** `8080`
- **Estado atual do repositório:** **Módulo 3 (Aulas 13, 14 e 15) 100% concluído e testado** (`docs/modelo.md` na **Versão 1.2**). Pronto para a **Aula 16 (AV2 — Seminário Técnico GoF + Mini-Projeto + Artigo IEEE)** e módulos seguintes.

---

## 2. ⚠️ Avisos Críticos do Spring Boot 4.1.1 (NÃO ALTERAR!)

1. **Artefato do H2 Console no `pom.xml`:**  
   No Spring Boot 4.1.1, o artefato real no Maven Central é **`org.springframework.boot:spring-boot-h2console`**. **NÃO troque** por `spring-boot-starter-h2-console` (esse nome não existe no Maven Central e quebra o build).
2. **Flyway no Spring Boot 4:**  
   É obrigatório manter `spring-boot-starter-flyway` (apenas `flyway-core` isolado não ativa a autoconfiguração no SB 4).
3. **Hibernate em modo `validate`:**  
   `spring.jpa.hibernate.ddl-auto=validate` está ativo no `application.properties`. Qualquer divergência entre entidades JPA e o esquema SQL aborta a subida da aplicação.
   - `Produto.sku` está alinhado com `@Column(nullable = false, unique = true, length = 30)` para bater com `VARCHAR(30)` da `V1`.
   - `Categoria` possui `@JsonIgnore` em `subcategorias` e `@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})` para evitar loop infinito de serialização JSON nos endpoints de `Produto` (a árvore completa continua sendo montada via `CategoriaArvoreDTO` em `GET /categorias/arvore`).
4. **Imutabilidade das Migrations (`src/main/resources/db/migration/`):**  
   **NUNCA edite** `V1`, `V2`, `V3` ou `V4`. Qualquer evolução futura de esquema deve ser criada como `V5__...sql` em diante.
5. **Retorno `401 Unauthorized` no Spring Security 7:**  
   Em `SecurityConfig`, está configurado `.exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))` para garantir `401` (em vez do `403` padrão do Spring quando não há formLogin/httpBasic) em requisições sem token ou com token inválido/expirado.

---

## 3. Histórico Completo do que está implementado (Aulas 06 a 15)

### Domínio, Regras de Negócio e Padrões GoF (Aulas 06–12)
- **Entidades JPA (`model/`):** `Categoria` (autorelacionamento Composite), `Produto`, `Cliente`, `Endereco`, `Carrinho`, `ItemCarrinho`, `Pedido`, `ItemPedido` e enum `PedidoStatus`.
- **Padrões GoF aplicados:**
  - **Singleton (escopo Spring):** `config/ConfiguracoesFeiraViva.java`
  - **Factory:** `factory/CupomFactory.java` + `model/cupom/{Cupom, CupomFixo, CupomPercentual}` (`FEIRA10` = 10%, `BEMVINDO` = R\$ 15,00 fixo)
  - **Composite:** Árvore de categorias recursiva em `CategoriaService.arvore()` (`GET /categorias/arvore`)
  - **Strategy:** Cálculo de frete (`PADRAO`, `FIXO`, `RETIRADA`) em `strategy/{StrategyFrete, CalculadoraFrete, FretePadraoStrategy, FreteFixoStrategy, FreteRetiradaStrategy}`
- **Regras de negócio concluídas:**
  - **R1 ✅:** Estoque verificado ao adicionar ao carrinho e ao finalizar pedido; devolvido no cancelamento.
  - **R2 ✅:** `total = subtotal + frete - desconto`.
  - **R3 ✅:** Senhas armazenadas exclusivamente com hash **BCrypt**.
  - **R4 ✅:** Cancelamento permitido apenas quando `status == CRIADO`.
  - **R5 ✅:** Autenticação stateless via **JWT (HS256)** e autorização por posse do recurso.
  - **R7 ✅:** Desconto fixo limitado ao subtotal.

### Aula 13 — Migrações com Flyway
- `V1__cria_esquema_inicial.sql`: tabelas `categorias`, `produtos`, `clientes`, `enderecos`, `carrinhos`, `itens_carrinho`, `pedidos`, `itens_pedido`.
- `V2__popula_dados_iniciais.sql`: categorias raiz/subcategorias, 5 produtos iniciais (`MEL-001` id=1 R\$ 35,00 estoque=12, `ALF-004`, `QUE-002`, `IOG-005`, `CAF-003`), cliente de teste **Maria Souza** (`id = 1`, `email = 'maria@email.com'`, `papel = 'CLIENTE'`) e endereço (`id = 1`, `cliente_id = 1`).
- `V3__ajustes_indices.sql`: índices de busca e constraint `ck_produto_estoque CHECK (estoque >= 0)`.
- Removidos `DataSeeder.java` e `H2ConsoleConfig.java`.

### Aula 14 — Spring Security & BCrypt
- `spring-boot-starter-security` adicionado; `PasswordEncoder` (`BCryptPasswordEncoder`) injetado em `ClienteService.criar()`.
- `V4__senha_cliente_em_bcrypt.sql`: migrou a senha da cliente de teste Maria (`maria@email.com` / senha em texto: `123456`) para hash BCrypt.

### Aula 15 — JWT: Autenticação Real (Fim do `?clienteId=`)
- **Pacote `security/`:**
  - `JwtService.java`: assina e valida tokens HS256 com claims `sub` (email), `clienteId` e `papel` (`app.jwt.secret` e `app.jwt.expiration-ms=3600000`).
  - `ClienteAutenticado.java`: record `(Long id, String email, String papel)` injetado nos controllers via `@AuthenticationPrincipal`.
  - `UsuarioDetailsService.java`: carrega `Cliente` por e-mail (`ClienteRepository.findByEmail`) com autoridade `ROLE_<papel>`.
  - `JwtAuthenticationFilter.java`: filtro `OncePerRequestFilter` que lê `Authorization: Bearer <token>`, valida a assinatura e popula o `SecurityContextHolder`.
- **Login e Tratamento de Erros:**
  - `POST /auth/login` em `controller/AuthController.java` usando `dto/LoginDTO.java` e `dto/TokenResponseDTO.java`.
  - `ApiErrorHandler.java`: trata `BadCredentialsException` retornando `401` (`{"erro":"credenciais_invalidas","mensagem":"E-mail ou senha inválidos"}`).
- **Controllers refatorados (sem `?clienteId=`):**
  - `CarrinhoController` (5 rotas) e `PedidoController` (4 rotas) usam `@AuthenticationPrincipal ClienteAutenticado cliente`.
  - `ClienteController` valida posse (`cliente.id().equals(id)`) em `GET /clientes/{id}` e `POST /clientes/{id}/enderecos`.
- **Swagger UI (`OpenApiConfig.java`):**
  - Configurado `SecurityScheme` HTTP Bearer JWT (`bearerAuth`), habilitando o botão **Authorize**.
- **Suíte de Testes Automatizados (`src/test/java/br/com/feiraviva/`):**
  - `FeiravivaApplicationTests`: subida de contexto + Flyway V1–V4 + validação Hibernate.
  - `GerarHashTest`: utilitário de geração BCrypt.
  - `JwtAuthIntegrationTest`: valida de ponta a ponta os **12 cenários (`T1` a `T12`)** da Aula 15 sobre HTTP real.

---

## 4. Como Rodar e Testar Rapidamente

```bash
# Rodar todos os testes automatizados (incluindo T1–T12 de JWT)
./mvnw test

# Subir a aplicação na porta 8080
./mvnw spring-boot:run
```

- **Credenciais de teste (seed):** `maria@email.com` / `123456`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html` (faça `POST /auth/login`, copie o `token` e cole no botão **Authorize**)
- **H2 Console:** `http://localhost:8080/h2-console` (`jdbc:h2:mem:feiraviva`, usuário `sa`, senha vazia)

---

## 5. Próximos Passos Previstos no Curso

- **Aula 16:** AV2 — Seminário Técnico GoF + Mini-Projeto + Artigo IEEE.
- **Módulo 4:** Frontend React consumindo a API com JWT.
- **Aulas futuras (Integrações, Pagamento e Deploy):**
  - Integração ViaCEP (`V5`), tabela de pagamentos 1:1 com pedidos (`V6`), migração para PostgreSQL em produção e uso da variável de ambiente `APP_JWT_SECRET` (Módulo 7).
