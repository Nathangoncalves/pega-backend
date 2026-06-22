package br.tcc.pega.service.impl;

import br.tcc.pega.dto.StudentDto;
import br.tcc.pega.entity.Student;
import br.tcc.pega.entity.User;
import br.tcc.pega.exception.ResourceNotFoundException;
import br.tcc.pega.repository.StudentRepository;
import br.tcc.pega.repository.UserRepository;
import br.tcc.pega.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository    userRepository;

    @Override
    @Transactional
    public StudentDto create(StudentDto dto) {
        User responsavel = userRepository.findById(dto.getResponsavelId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", dto.getResponsavelId()));

        Student student = Student.builder()
                .nome(dto.getNome())
                .responsavel(responsavel)
                .nivelAtual(dto.getNivelAtual() != null ? dto.getNivelAtual() : 1)
                .scoreTotal(dto.getScoreTotal() != null ? dto.getScoreTotal() : 0)
                .build();

        return toDto(studentRepository.save(student));
    }

    @Override
    @Transactional
    public StudentDto update(UUID id, StudentDto dto) {
        Student student = getEntityById(id);
        student.setNome(dto.getNome());
        if (dto.getNivelAtual() != null) student.setNivelAtual(dto.getNivelAtual());
        if (dto.getScoreTotal() != null) student.setScoreTotal(dto.getScoreTotal());
        return toDto(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        // JPA cascateia automaticamente: Student → GameResult (cascade REMOVE) → Feedback (cascade REMOVE)
        studentRepository.delete(getEntityById(id));
    }

    @Override
    public StudentDto findById(UUID id) {
        return toDto(getEntityById(id));
    }

    @Override
    public Page<StudentDto> findAll(Pageable pageable) {
        return studentRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<StudentDto> findByResponsavelId(UUID responsavelId, Pageable pageable) {
        return studentRepository.findByResponsavelId(responsavelId, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public StudentDto updateScore(UUID studentId, Integer points) {
        Student student = getEntityById(studentId);
        student.setScoreTotal(student.getScoreTotal() + points);
        student.setNivelAtual((student.getScoreTotal() / 100) + 1);
        return toDto(studentRepository.save(student));
    }

    private Student getEntityById(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }

    private StudentDto toDto(Student s) {
        return StudentDto.builder()
                .id(s.getId())
                .nome(s.getNome())
                .nivelAtual(s.getNivelAtual())
                .scoreTotal(s.getScoreTotal())
                .responsavelId(s.getResponsavel().getId())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
