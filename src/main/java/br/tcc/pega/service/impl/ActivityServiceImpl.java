package br.tcc.pega.service.impl;

import br.tcc.pega.dto.ActivityDto;
import br.tcc.pega.entity.Activity;
import br.tcc.pega.entity.Activity.ActivityType;
import br.tcc.pega.exception.ResourceNotFoundException;
import br.tcc.pega.repository.ActivityRepository;
import br.tcc.pega.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Override
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

    @Override
    @Transactional
    public ActivityDto update(UUID id, ActivityDto dto) {
        Activity activity = getEntityById(id);
        activity.setNome(dto.getNome());
        activity.setTipo(ActivityType.valueOf(dto.getTipo().toUpperCase()));
        activity.setDescricao(dto.getDescricao());
        activity.setDificuldade(dto.getDificuldade());
        if (dto.getAtivo() != null) activity.setAtivo(dto.getAtivo());
        return toDto(activityRepository.save(activity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        activityRepository.delete(getEntityById(id));
    }

    @Override
    public ActivityDto findById(UUID id) {
        return toDto(getEntityById(id));
    }

    @Override
    public Page<ActivityDto> findAll(Pageable pageable) {
        return activityRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<ActivityDto> findAllAtivas(Pageable pageable) {
        return activityRepository.findByAtivoTrue(pageable).map(this::toDto);
    }

    @Override
    public Page<ActivityDto> findByTipo(ActivityType tipo, Pageable pageable) {
        return activityRepository.findByTipoAndAtivoTrue(tipo, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        Activity activity = getEntityById(id);
        activity.setAtivo(false);
        activityRepository.save(activity);
    }

    private Activity getEntityById(UUID id) {
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
