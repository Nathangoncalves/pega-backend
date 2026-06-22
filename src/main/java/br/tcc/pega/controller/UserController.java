package br.tcc.pega.controller;

import br.tcc.pega.dto.StudentDto;
import br.tcc.pega.dto.UserDto;
import br.tcc.pega.entity.User;
import br.tcc.pega.repository.UserRepository;
import br.tcc.pega.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Usuário", description = "Perfil do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;
    private final StudentService studentService;

    @GetMapping("/profile")
    @Operation(summary = "Retorna o perfil do usuário logado")
    public ResponseEntity<UserDto> getProfile(@AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return ResponseEntity.ok(UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nome(user.getNome())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build());
    }

    @GetMapping("/students")
    @Operation(summary = "Lista os alunos vinculados ao usuário autenticado")
    public ResponseEntity<Page<StudentDto>> getMyStudents(
            @AuthenticationPrincipal UserDetails principal,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return ResponseEntity.ok(studentService.findByResponsavelId(user.getId(), pageable));
    }
}
