package br.tcc.pega.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resultado de uma palavra individual dentro de uma sessão de jogo */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PalavraDetalheDto {
    /** A palavra que foi apresentada ao aluno (ex: "bola") */
    private String palavra;
    /** true = acertou, false = errou */
    private Boolean acertou;
    /** Tempo gasto nesta palavra em milissegundos */
    private Long tempoMs;
}
