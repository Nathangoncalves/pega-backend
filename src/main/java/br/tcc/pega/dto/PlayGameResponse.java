package br.tcc.pega.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PlayGameResponse {

    private Long gameResultId;
    private Integer acertos;
    private Integer erros;
    private Long tempoMs;
    private Integer novoScore;
    private Integer novoNivel;
    private String feedback;
    private String feedbackTipo;
    /** Próxima atividade sugerida pelo AdaptationAgent + ContentAgent */
    private ActivityDto proximaAtividade;
}
