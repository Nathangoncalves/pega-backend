package br.tcc.pega.dto;

import br.tcc.pega.entity.User.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 50, message = "Senha deve ter entre 6 e 50 caracteres")
    private String senha;

    @NotNull(message = "Perfil é obrigatório: PROFESSOR, TERAPEUTA ou ADMIN")
    private UserRole role;
}
