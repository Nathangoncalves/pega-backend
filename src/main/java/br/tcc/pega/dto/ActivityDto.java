package br.tcc.pega.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityDto {

    private Long id;

    @NotBlank(message = "Nome da atividade é obrigatório")
    @Size(min = 2, max = 150)
    private String nome;

    @NotBlank(message = "Tipo é obrigatório: LEITURA, ESCRITA, FONETICA, VOCABULARIO ou SILABAS")
    private String tipo;

    private String descricao;

    @NotNull(message = "Dificuldade é obrigatória")
    @Min(value = 1, message = "Dificuldade mínima é 1")
    @Max(value = 5, message = "Dificuldade máxima é 5")
    private Integer dificuldade;

    private Boolean ativo;
}
