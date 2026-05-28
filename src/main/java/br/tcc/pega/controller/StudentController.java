package br.tcc.pega.controller;

import br.tcc.pega.dto.StudentDto;
import br.tcc.pega.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Alunos", description = "Gerenciamento de alunos (crianças)")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar aluno por ID")
    public ResponseEntity<StudentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo aluno vinculado a um professor/terapeuta")
    public ResponseEntity<StudentDto> create(@Valid @RequestBody StudentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(dto));
    }
}
