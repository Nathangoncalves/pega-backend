package br.tcc.pega.agent.dto;

import lombok.*;

/** Resposta do ProgressAgent após salvar resultado e atualizar score */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressOutput {
    private Long    gameResultId;
    private Integer novoScore;
    private Integer novoNivel;
}
