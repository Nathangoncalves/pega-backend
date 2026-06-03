package br.tcc.pega.service;

import br.tcc.pega.dto.GameResultDto;
import br.tcc.pega.entity.Activity;
import br.tcc.pega.entity.GameResult;
import br.tcc.pega.entity.Student;
import br.tcc.pega.exception.ResourceNotFoundException;
import br.tcc.pega.repository.ActivityRepository;
import br.tcc.pega.repository.GameResultRepository;
import br.tcc.pega.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameResultService {

    private final GameResultRepository gameResultRepository;
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final StudentService studentService;

    /**
     * Persiste o resultado da sessão e atualiza o score/nível do aluno.
     * Cada acerto vale 1 ponto — a lógica de gamificação pode ser expandida aqui.
     */
    @Transactional
    public GameResultDto save(GameResultDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", dto.getStudentId()));

        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Atividade", dto.getActivityId()));

        GameResult result = GameResult.builder()
                .student(student)
                .activity(activity)
                .acertos(dto.getAcertos())
                .erros(dto.getErros())
                .tempoMs(dto.getTempoMs())
                .timestamp(LocalDateTime.now())
                .build();

        result = gameResultRepository.save(result);

        // Gamificação: acertos acumulam score e evoluem o nível
        studentService.updateScore(student.getId(), dto.getAcertos());

        return toDto(result);
    }

    public List<GameResultDto> findByStudentId(Long studentId) {
        return gameResultRepository.findByStudentIdOrderByTimestampDesc(studentId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public GameResultDto findById(Long id) {
        return toDto(gameResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado", id)));
    }

    private GameResultDto toDto(GameResult r) {
        return GameResultDto.builder()
                .id(r.getId())
                .studentId(r.getStudent().getId())
                .studentNome(r.getStudent().getNome())
                .activityId(r.getActivity().getId())
                .activityNome(r.getActivity().getNome())
                .activityTipo(r.getActivity().getTipo().name())
                .acertos(r.getAcertos())
                .erros(r.getErros())
                .tempoMs(r.getTempoMs())
                .detalhesPalavras(r.getDetalhesPalavras())
                .timestamp(r.getTimestamp())
                .build();
    }
}
