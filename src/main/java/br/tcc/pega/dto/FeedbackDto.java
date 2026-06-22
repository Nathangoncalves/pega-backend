package br.tcc.pega.dto;

import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FeedbackDto {

    private UUID id;
    private UUID gameResultId;
    private String mensagem;
    private String tipo;
}
