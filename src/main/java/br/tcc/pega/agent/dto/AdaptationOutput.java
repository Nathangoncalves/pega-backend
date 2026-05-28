package br.tcc.pega.agent.dto;

import lombok.*;

/** Resultado produzido pelo AdaptationAgent: dificuldade recomendada para próxima atividade */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AdaptationOutput {
    private Integer dificuldadeRecomendada;
}
