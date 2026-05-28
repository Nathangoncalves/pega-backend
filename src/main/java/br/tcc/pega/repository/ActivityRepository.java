package br.tcc.pega.repository;

import br.tcc.pega.entity.Activity;
import br.tcc.pega.entity.Activity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByAtivoTrue();

    List<Activity> findByDificuldade(Integer dificuldade);

    List<Activity> findByTipoAndAtivoTrue(ActivityType tipo);
}
