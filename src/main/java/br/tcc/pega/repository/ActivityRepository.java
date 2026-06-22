package br.tcc.pega.repository;

import br.tcc.pega.entity.Activity;
import br.tcc.pega.entity.Activity.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {

    Page<Activity> findByAtivoTrue(Pageable pageable);

    Page<Activity> findByTipoAndAtivoTrue(ActivityType tipo, Pageable pageable);

    Page<Activity> findByDificuldade(Integer dificuldade, Pageable pageable);
}
