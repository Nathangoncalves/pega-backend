package br.tcc.pega.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlayGameRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private Long activityId;

    @NotNull @Min(0)
    private Integer acertos;

    @NotNull @Min(0)
    private Integer erros;

    @NotNull @Min(0)
    private Long tempoMs;

    /** Preenchido pelo servidor antes de enviar ao agente — não validado pelo cliente */
    private String sessionId;
}
