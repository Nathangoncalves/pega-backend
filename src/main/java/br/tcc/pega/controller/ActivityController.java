package br.tcc.pega.controller;

import br.tcc.pega.dto.ActivityDto;
import br.tcc.pega.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(name = "Atividades", description = "Atividades de alfabetização disponíveis no jogo")
@SecurityRequirement(name = "bearerAuth")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    @Operation(summary = "Listar todas as atividades ativas")
    public ResponseEntity<List<ActivityDto>> listAll() {
        return ResponseEntity.ok(activityService.findAllAtivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar atividade por ID")
    public ResponseEntity<ActivityDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova atividade (PROFESSOR ou ADMIN)")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    public ResponseEntity<ActivityDto> create(@Valid @RequestBody ActivityDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar atividade existente")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'ADMIN')")
    public ResponseEntity<ActivityDto> update(@PathVariable Long id,
                                              @Valid @RequestBody ActivityDto dto) {
        return ResponseEntity.ok(activityService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar atividade (soft delete, somente ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        activityService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
