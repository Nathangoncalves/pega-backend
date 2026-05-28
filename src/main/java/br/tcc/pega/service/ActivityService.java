package br.tcc.pega.service;

import br.tcc.pega.dto.ActivityDto;
import br.tcc.pega.entity.Activity;
import br.tcc.pega.entity.Activity.ActivityType;
import br.tcc.pega.exception.ResourceNotFoundException;
import br.tcc.pega.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    public List<ActivityDto> findAllAtivas() {
        return activityRepository.findByAtivoTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public ActivityDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public ActivityDto create(ActivityDto dto) {
        Activity activity = Activity.builder()
                .nome(dto.getNome())
                .tipo(ActivityType.valueOf(dto.getTipo().toUpperCase()))
                .descricao(dto.getDescricao())
                .dificuldade(dto.getDificuldade())
                .ativo(dto.getAtivo() != null ? dto.getAtivo() : true)
                .build();
        return toDto(activityRepository.save(activity));
    }

    @Transactional
    public ActivityDto update(Long id, ActivityDto dto) {
        Activity activity = getOrThrow(id);
        activity.setNome(dto.getNome());
        activity.setTipo(ActivityType.valueOf(dto.getTipo().toUpperCase()));
        activity.setDescricao(dto.getDescricao());
        activity.setDificuldade(dto.getDificuldade());
        if (dto.getAtivo() != null) activity.setAtivo(dto.getAtivo());
        return toDto(activityRepository.save(activity));
    }

    /** Soft delete — desativa a atividade sem removê-la do banco */
    @Transactional
    public void deactivate(Long id) {
        Activity activity = getOrThrow(id);
        activity.setAtivo(false);
        activityRepository.save(activity);
    }

    private Activity getOrThrow(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atividade", id));
    }

    private ActivityDto toDto(Activity a) {
        return ActivityDto.builder()
                .id(a.getId())
                .nome(a.getNome())
                .tipo(a.getTipo().name())
                .descricao(a.getDescricao())
                .dificuldade(a.getDificuldade())
                .ativo(a.getAtivo())
                .build();
    }
}
