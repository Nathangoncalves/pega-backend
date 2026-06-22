package br.tcc.pega.controller;

import br.tcc.pega.dto.GameResultDto;
import br.tcc.pega.service.GameResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/game-results")
@RequiredArgsConstructor
@Tag(name = "Resultados do Jogo", description = "Registro e histórico de sessões de jogo")
@SecurityRequirement(name = "bearerAuth")
public class GameResultController {

    private final GameResultService gameResultService;

    @PostMapping
    @Operation(summary = "Salvar resultado de uma sessão — atualiza score e nível do aluno automaticamente")
    public ResponseEntity<GameResultDto> save(@Valid @RequestBody GameResultDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameResultService.create(dto));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Histórico de resultados de um aluno (ordem decrescente por data)")
    public ResponseEntity<Page<GameResultDto>> getByStudent(
            @PathVariable UUID studentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(gameResultService.findByStudentId(studentId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar resultado específico por ID")
    public ResponseEntity<GameResultDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(gameResultService.findById(id));
    }
}
