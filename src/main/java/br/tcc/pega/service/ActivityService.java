package br.tcc.pega.service;

import br.tcc.pega.dto.ActivityDto;
import br.tcc.pega.entity.Activity;
import br.tcc.pega.entity.Activity.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ActivityService extends CrudService<Activity, ActivityDto> {

    Page<ActivityDto> findAllAtivas(Pageable pageable);

    Page<ActivityDto> findByTipo(ActivityType tipo, Pageable pageable);

    void deactivate(UUID id);
}
