package br.tcc.pega.repository;

import br.tcc.pega.entity.GameResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameResultRepository extends JpaRepository<GameResult, UUID> {

    Page<GameResult> findByStudentIdOrderByTimestampDesc(UUID studentId, Pageable pageable);

    Page<GameResult> findByStudentIdAndActivityId(UUID studentId, UUID activityId, Pageable pageable);
}
