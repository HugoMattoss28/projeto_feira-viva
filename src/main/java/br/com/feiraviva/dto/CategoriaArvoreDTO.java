package br.com.feiraviva.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Nó da árvore de categorias (padrão Composite)")
public record CategoriaArvoreDTO(
        @Schema(description = "ID da categoria", example = "1") Long id,
        @Schema(description = "Nome da categoria", example = "Hortaliças") String nome,
        @Schema(description = "Descrição da categoria", example = "Verduras frescas") String descricao,
        @Schema(description = "ID da categoria pai (null se for raiz)", example = "null") Long categoriaPaiId,
        @Schema(description = "Indica se é o último nível", example = "true") boolean folha,
        @Schema(description = "Lista de subcategorias") List<CategoriaArvoreDTO> subcategorias) { }