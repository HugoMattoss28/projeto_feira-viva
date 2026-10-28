# Feira Viva — Modelo de Domínio

> Documento vivo do modelo de domínio do projeto.
> **Stack:** Java 21 LTS • Spring Boot 4.1.1 • Maven • React
> **Pacote base:** `br.com.feiraviva`
> **Versão:** 1.2 (JWT HS256, `/auth/login`, R3/R5 concluídas e remoção do `?clienteId=` — Aula 15) • **Versão-base:** Aula 05 • **Início do código:** Aula 06 • **Manutenção:** equipes + professor.

## 1. Identificação

| Item | Definição |
|---|---|
| **Projeto** | Feira Viva — loja online de produtos locais |
| **Arquitetura** | Monólito modular baseado em MVC |
| **Stack Backend** | Java 21 (LTS) · Spring Boot 4.1.1 · Spring Security 7 · Flyway · Maven |
| **Stack Frontend** | React (Vite) |
| **Pacote base** | `br.com.feiraviva` |
| **Model** | Entidades + regras de negócio (Spring/Jakarta) |
| **View** | Interface React |
| **Controller** | Rotas/endpoints (Spring MVC) |
| **Parceiro** | Associação de Produtores Locais |
| **Problema real** | Produtores vendem apenas na feira presencial; a comunidade não compra fora do horário da feira |
| **Público-alvo** | Moradores da região e pequenos produtores |

## 2. MVP

Compra de ponta a ponta: catálogo → detalhe do produto → carrinho → login/cadastro → endereço de entrega → confirmação do pedido (pagamento simulado).

## 3. Entidades (Model)

| Entidade | Principais atributos | Relacionamentos |
|---|---|---|
| **Produto** | id, nome, descricao, preco, sku, estoque, ativo, urlImagem | N:1 Categoria |
| **Categoria** | id, nome, descricao, categoriaPai | 1:N Produto; N:1 Categoria (hierarquia) |
| **Cliente** | id, nome, email, senhaHash, telefone, papel | 1:N Endereco; 1:N Pedido; 1:(0..1) Carrinho |
| **Endereco** | id, cep, logradouro, numero, complemento, bairro, cidade, uf | N:1 Cliente; 1:N Pedido |
| **Carrinho** | id, total (calculado), dataCriacao, codigoCupom, estrategiaFrete | 1:N ItemCarrinho; (0..1):1 Cliente |
| **ItemCarrinho** | id, quantidade, precoUnitario | N:1 Carrinho; N:1 Produto |
| **Pedido** | id, numero, status, subtotal, frete, total, dataCriacao | N:1 Cliente; N:1 Endereco; 1:N ItemPedido; 1:1 Pagamento |
| **ItemPedido** | id, quantidade, precoUnitario (snapshot) | N:1 Pedido; N:1 Produto |
| **Pagamento** | id, metodo, status, idTransacao | 1:1 Pedido |

### Enums

- `Papel`: `CLIENTE`, `ADMIN`
- `PedidoStatus`: `CRIADO`, `PAGO`, `EM_PREPARO`, `ENVIADO`, `ENTREGUE`, `CANCELADO`
- `MetodoPagamento`: `CARTAO`, `PIX`, `BOLETO`
- `PagamentoStatus`: `APROVADO`, `RECUSADO`, `PENDENTE`

## 4. Diagrama de Relacionamentos

```text
Cliente (1) ──── (N) Endereco
Cliente (1) ──── (N) Pedido
Cliente (1) ──── (0..1) Carrinho
Carrinho (1) ──── (N) ItemCarrinho (N) ──── (1) Produto
Pedido (1) ──── (N) ItemPedido (N) ──── (1) Produto
Pedido (1) ──── (1) Pagamento
Pedido (N) ──── (1) Endereco
Produto (N) ──── (1) Categoria
Categoria (N) ──── (1) Categoria (categoriaPai)
```

## 5. Segurança, Regras e Decisões Arquiteturais

- **R1 ✅:** Estoque verificado ao adicionar ao carrinho e ao finalizar pedido; devolvido no cancelamento.
- **R2 ✅:** `total = subtotal + frete - desconto`.
- **R3 ✅:** Senhas armazenadas exclusivamente com hash **BCrypt** (cadastro + migration `V4`).
- **R4 ✅:** Cancelamento permitido apenas para pedidos com status `CRIADO`.
- **R5 ✅:** Autenticação stateless via **JWT (HS256)** na rota `POST /auth/login`; filtro `JwtAuthenticationFilter` popula `@AuthenticationPrincipal ClienteAutenticado`.
- **R7 ✅:** Desconto fixo nunca supera o subtotal.
- **Dívida técnica paga (Aula 15):** Parâmetro provisório `?clienteId=` removido de todas as rotas de `CarrinhoController` e `PedidoController`; verificação de posse ativa em `ClienteController`.
- **Decisão de Produção (Módulo 7):** Em desenvolvimento, `app.jwt.secret` fica no `application.properties`; em produção, o segredo deve vir obrigatoriamente da variável de ambiente **`APP_JWT_SECRET`** (nunca commitado no repositório).
