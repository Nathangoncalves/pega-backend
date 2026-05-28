package br.tcc.pega.agent.dto;

import lombok.*;

/** Resultado produzido pelo AssessmentAgent após análise de uma sessão de jogo */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AssessmentOutput {
    private Integer pontuacao;
    private String  feedback;
    private String  feedbackTipo;
}
