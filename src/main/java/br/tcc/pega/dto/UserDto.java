package br.tcc.pega.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {

    private UUID id;
    private String email;
    private String nome;
    private String role;
    private LocalDateTime createdAt;
}
