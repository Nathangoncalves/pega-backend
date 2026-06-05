package br.tcc.pega.service;

import br.tcc.pega.dto.StudentDto;
import br.tcc.pega.entity.Student;
import br.tcc.pega.entity.User;
import br.tcc.pega.exception.ResourceNotFoundException;
import br.tcc.pega.repository.StudentRepository;
import br.tcc.pega.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public List<StudentDto> findByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public StudentDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public StudentDto create(StudentDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", dto.getUserId()));

        Student student = Student.builder()
                .nome(dto.getNome())
                .user(user)
                .nivelAtual(dto.getNivelAtual() != null ? dto.getNivelAtual() : 1)
                .scoreTotal(dto.getScoreTotal() != null ? dto.getScoreTotal() : 0)
                .build();

        return toDto(studentRepository.save(student));
    }

    /**
     * Soma pontos ao score do aluno e recalcula o nível.
     * Regra: nível = (scoreTotal / 100) + 1  — cada 100 pontos avança um nível.
     */
    @Transactional
    public StudentDto updateScore(Long studentId, Integer pontosGanhos) {
        Student student = getOrThrow(studentId);
        student.setScoreTotal(student.getScoreTotal() + pontosGanhos);
        student.setNivelAtual((student.getScoreTotal() / 100) + 1);
        return toDto(studentRepository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.delete(getOrThrow(id));
    }

    private Student getOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }

    private StudentDto toDto(Student s) {
        return StudentDto.builder()
                .id(s.getId())
                .nome(s.getNome())
                .nivelAtual(s.getNivelAtual())
                .scoreTotal(s.getScoreTotal())
                .userId(s.getUser().getId())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
