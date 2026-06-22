package br.tcc.pega.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PlayGameRequest {

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID activityId;

    @NotNull @Min(0)
    private Integer acertos;

    @NotNull @Min(0)
    private Integer erros;

    @NotNull @Min(0)
    private Long tempoMs;

    /** Detalhamento por palavra: tempo e resultado de cada item da sessão */
    private List<PalavraDetalheDto> palavras;

    /** Preenchido pelo servidor antes de enviar ao agente — não validado pelo cliente */
    private String sessionId;
}
