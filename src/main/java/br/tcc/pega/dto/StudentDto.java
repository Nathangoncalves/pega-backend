package br.tcc.pega.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDto {

    private UUID id;

    @NotBlank(message = "Nome do aluno é obrigatório")
    @Size(min = 2, max = 100)
    private String nome;

    private Integer nivelAtual;
    private Integer scoreTotal;

    @NotNull(message = "responsavelId do professor/terapeuta é obrigatório")
    private UUID responsavelId;

    private LocalDateTime createdAt;
}
