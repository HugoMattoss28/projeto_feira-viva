package br.com.feiraviva.controller;

import br.com.feiraviva.model.Produto;
import br.com.feiraviva.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Catálogo")
public class ProdutoController {

    private final ProdutoService produtoService;

    // Injeção de dependência via construtor
    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna o catálogo completo de produtos disponíveis.")
    @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    public List<Produto> listar() {
        return produtoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um produto específico através do seu identificador.")
    @ApiResponse(responseCode = "200", description = "Produto encontrado")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    public Produto buscar(
            @Parameter(description = "ID do produto", required = true, example = "1")
            @PathVariable Long id) {
        return produtoService.buscar(id);
    }

}