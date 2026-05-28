package br.tcc.pega.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FeedbackDto {

    private Long id;
    private Long gameResultId;
    private String mensagem;
    private String tipo;
}
