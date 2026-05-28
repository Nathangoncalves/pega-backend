package br.tcc.pega.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {

    private Long id;
    private String email;
    private String nome;
    private String role;
    private LocalDateTime createdAt;
}
