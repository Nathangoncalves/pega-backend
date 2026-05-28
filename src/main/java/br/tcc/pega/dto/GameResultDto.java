package br.tcc.pega.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class GameResultDto {

    private Long id;

    @NotNull(message = "studentId é obrigatório")
    private Long studentId;

    @NotNull(message = "activityId é obrigatório")
    private Long activityId;

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
}
