package br.tcc.pega.repository;

import br.tcc.pega.entity.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    List<GameResult> findByStudentIdOrderByTimestampDesc(Long studentId);

    List<GameResult> findByStudentIdAndActivityId(Long studentId, Long activityId);
}
