package br.tcc.pega.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentDto {

    private Long id;

    @NotBlank(message = "Nome do aluno é obrigatório")
    @Size(min = 2, max = 100)
    private String nome;

    private Integer nivelAtual;
    private Integer scoreTotal;

    @NotNull(message = "userId do responsável é obrigatório")
    private Long userId;

    private LocalDateTime createdAt;
}
