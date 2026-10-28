package br.com.feiraviva;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtAuthIntegrationTest {

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();
    private static String tokenMaria;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    @Order(1)
    void t1_loginValidoRetornaToken() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/auth/login")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"email":"maria@email.com","senha":"123456"}
                        """))
                .build();

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(200);
        tokenMaria = JsonPath.read(res.body(), "$.token");
        assertThat(tokenMaria).isNotBlank();
        assertThat(((Number) JsonPath.read(res.body(), "$.clienteId")).longValue()).isEqualTo(1L);
        assertThat((String) JsonPath.read(res.body(), "$.email")).isEqualTo("maria@email.com");
    }

    @Test
    @Order(2)
    void t2_loginComSenhaErradaRetorna401() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/auth/login")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"email":"maria@email.com","senha":"errada"}
                        """))
                .build();

        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(401);
        assertThat((String) JsonPath.read(res.body(), "$.erro")).isEqualTo("credenciais_invalidas");
    }

    @Test
    @Order(3)
    void t3_carrinhoSemTokenRetorna401() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/carrinho"))).GET().build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(401);
    }

    @Test
    @Order(4)
    void t4_carrinhoComBearerRetorna200() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/carrinho")))
                .header("Authorization", "Bearer " + tokenMaria)
                .GET()
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(5)
    void t5_adicionarItemCarrinhoRetorna201ComSubtotal70() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/carrinho/itens")))
                .header("Authorization", "Bearer " + tokenMaria)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"produtoId":1,"quantidade":2}
                        """))
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(201);
        assertThat(((Number) JsonPath.read(res.body(), "$.subtotal")).doubleValue()).isEqualTo(70.00);
    }

    @Test
    @Order(6)
    void t6_aplicarCupomFeira10RetornaDesconto7() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/carrinho/cupom?codigo=FEIRA10")))
                .header("Authorization", "Bearer " + tokenMaria)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(((Number) JsonPath.read(res.body(), "$.desconto")).doubleValue()).isEqualTo(7.00);
    }

    @Test
    @Order(7)
    void t7_definirFreteFixoRetornaFrete15() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/carrinho/frete?tipo=FIXO")))
                .header("Authorization", "Bearer " + tokenMaria)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(200);
        assertThat(((Number) JsonPath.read(res.body(), "$.frete")).doubleValue()).isEqualTo(15.00);
    }

    @Test
    @Order(8)
    void t8_finalizarPedidoRetorna201EBaixaEstoque() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/pedidos")))
                .header("Authorization", "Bearer " + tokenMaria)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {"enderecoId":1}
                        """))
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(201);
        assertThat((String) JsonPath.read(res.body(), "$.numero")).isEqualTo("FV-0001");

        var reqProd = HttpRequest.newBuilder(URI.create(url("/produtos/1"))).GET().build();
        var resProd = client.send(reqProd, HttpResponse.BodyHandlers.ofString());
        assertThat(((Number) JsonPath.read(resProd.body(), "$.estoque")).intValue()).isEqualTo(10);
    }

    @Test
    @Order(9)
    void t9_historicoPedidosRetorna200() throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url("/pedidos")))
                .header("Authorization", "Bearer " + tokenMaria)
                .GET()
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(200);
        assertThat((int) JsonPath.read(res.body(), "$.length()")).isEqualTo(1);
    }

    @Test
    @Order(10)
    void t10_tokenAdulteradoRetorna401() throws Exception {
        int pos = tokenMaria.length() - 5;
        char c = tokenMaria.charAt(pos);
        String adulterado = tokenMaria.substring(0, pos)
                + (c == 'A' ? 'Z' : 'A')
                + tokenMaria.substring(pos + 1);

        var req = HttpRequest.newBuilder(URI.create(url("/carrinho")))
                .header("Authorization", "Bearer " + adulterado)
                .GET()
                .build();
        var res = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertThat(res.statusCode()).isEqualTo(401);
    }

    @Test
    @Order(11)
    void t11_rotasPublicasProdutosECategoriasSemTokenRetornam200() throws Exception {
        var resProd = client.send(HttpRequest.newBuilder(URI.create(url("/produtos"))).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(resProd.statusCode()).isEqualTo(200);

        var resCat = client.send(HttpRequest.newBuilder(URI.create(url("/categorias/arvore"))).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(resCat.statusCode()).isEqualTo(200);
    }

    @Test
    @Order(12)
    void t12_cadastroPublicoEVerificacaoDePosseNoPerfil() throws Exception {
        var reqCadastro = HttpRequest.newBuilder(URI.create(url("/clientes")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                          "nome": "João Silva",
                          "email": "joao@email.com",
                          "senha": "654321",
                          "telefone": "(11) 97777-6666",
                          "enderecos": []
                        }
                        """))
                .build();
        var resCadastro = client.send(reqCadastro, HttpResponse.BodyHandlers.ofString());
        assertThat(resCadastro.statusCode()).isEqualTo(201);
        long idJoao = ((Number) JsonPath.read(resCadastro.body(), "$.id")).longValue();

        // Maria tentando consultar o perfil do João -> 409 RegraDeNegocioException
        var reqOutroPerfil = HttpRequest.newBuilder(URI.create(url("/clientes/" + idJoao)))
                .header("Authorization", "Bearer " + tokenMaria)
                .GET()
                .build();
        var resOutroPerfil = client.send(reqOutroPerfil, HttpResponse.BodyHandlers.ofString());
        assertThat(resOutroPerfil.statusCode()).isEqualTo(409);

        // Maria consultando o próprio perfil -> 200
        var reqProprioPerfil = HttpRequest.newBuilder(URI.create(url("/clientes/1")))
                .header("Authorization", "Bearer " + tokenMaria)
                .GET()
                .build();
        var resProprioPerfil = client.send(reqProprioPerfil, HttpResponse.BodyHandlers.ofString());
        assertThat(resProprioPerfil.statusCode()).isEqualTo(200);
    }
}

