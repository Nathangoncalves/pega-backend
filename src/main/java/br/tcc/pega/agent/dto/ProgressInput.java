package br.tcc.pega.agent.dto;

import lombok.*;

/** Dados enviados ao ProgressAgent para persistência no banco */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgressInput {
    private String  sessionId;
    private Long    studentId;
    private Long    activityId;
    private Integer acertos;
    private Integer erros;
    private Long    tempoMs;
    private Integer pontuacao;
    private String  feedback;
    private String  feedbackTipo;
}
