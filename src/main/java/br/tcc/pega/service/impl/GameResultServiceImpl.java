package br.tcc.pega.service.impl;

import br.tcc.pega.dto.GameResultDto;
import br.tcc.pega.entity.Activity;
import br.tcc.pega.entity.GameResult;
import br.tcc.pega.entity.Student;
import br.tcc.pega.exception.ResourceNotFoundException;
import br.tcc.pega.repository.ActivityRepository;
import br.tcc.pega.repository.FeedbackRepository;
import br.tcc.pega.repository.GameResultRepository;
import br.tcc.pega.repository.StudentRepository;
import br.tcc.pega.service.GameResultService;
import br.tcc.pega.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameResultServiceImpl implements GameResultService {

    private final GameResultRepository gameResultRepository;
    private final StudentRepository    studentRepository;
    private final ActivityRepository   activityRepository;
    private final FeedbackRepository   feedbackRepository;
    private final StudentService       studentService;

    @Override
    @Transactional
    public GameResultDto create(GameResultDto dto) {
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
                .detalhesPalavras(dto.getDetalhesPalavras())
                .timestamp(LocalDateTime.now())
                .build();

        result = gameResultRepository.save(result);
        studentService.updateScore(student.getId(), dto.getAcertos());

        return toDto(result);
    }

    @Override
    public GameResultDto update(UUID id, GameResultDto dto) {
        throw new UnsupportedOperationException("Resultados de jogo são imutáveis");
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GameResult result = getEntityById(id);
        feedbackRepository.findByGameResultId(id).ifPresent(feedbackRepository::delete);
        gameResultRepository.delete(result);
    }

    @Override
    public GameResultDto findById(UUID id) {
        return toDto(getEntityById(id));
    }

    @Override
    public Page<GameResultDto> findAll(Pageable pageable) {
        return gameResultRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<GameResultDto> findByStudentId(UUID studentId, Pageable pageable) {
        return gameResultRepository
                .findByStudentIdOrderByTimestampDesc(studentId, pageable)
                .map(this::toDto);
    }

    private GameResult getEntityById(UUID id) {
        return gameResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultado", id));
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
