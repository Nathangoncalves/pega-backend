package br.tcc.pega.controller;

import br.tcc.pega.dto.StudentDto;
import br.tcc.pega.service.StudentService;
import br.tcc.pega.service.impl.StudentServiceImpl;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gerenciamento de alunos (crianças)")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/responsavel/{responsavelId}")
    @Operation(summary = "Listar alunos por professor/terapeuta responsável")
    public ResponseEntity<Page<StudentDto>> findByResponsavel(
            @PathVariable UUID responsavelId,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(studentService.findByResponsavelId(responsavelId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por ID")
    public ResponseEntity<StudentDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos os alunos paginado")
    public ResponseEntity<Page<StudentDto>> findAll(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(studentService.findAll(pageable));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo aluno vinculado a um professor/terapeuta")
    public ResponseEntity<StudentDto> create(@Valid @RequestBody StudentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do aluno")
    public ResponseEntity<StudentDto> update(@PathVariable UUID id,
                                             @Valid @RequestBody StudentDto dto) {
        return ResponseEntity.ok(studentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir aluno e todos os seus resultados")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
