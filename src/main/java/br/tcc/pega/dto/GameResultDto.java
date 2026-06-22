package br.tcc.pega.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GameResultDto {

    private UUID id;

    @NotNull(message = "studentId é obrigatório")
    private UUID studentId;

    @NotNull(message = "activityId é obrigatório")
    private UUID activityId;

    @NotNull @Min(0)
    private Integer acertos;

    @NotNull @Min(0)
    private Integer erros;

    @NotNull @Min(0)
    private Long tempoMs;

    private LocalDateTime timestamp;

    /** Campos somente leitura — preenchidos na resposta */
    private String activityNome;
    private String activityTipo;
    private String studentNome;

    /** JSON com detalhamento por palavra: [{palavra, acertou, tempoMs}, ...] */
    private String detalhesPalavras;
}
