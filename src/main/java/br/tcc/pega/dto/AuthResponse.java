package br.tcc.pega.dto;

import lombok.*;

import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {

    private String token;
    private String tipo;
    private UUID userId;
    private String nome;
    private String email;
    private String role;
    /** Expiração em milissegundos a partir da emissão */
    private Long expiresIn;
}
