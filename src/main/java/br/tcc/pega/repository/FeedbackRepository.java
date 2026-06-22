package br.tcc.pega.repository;

import br.tcc.pega.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    Optional<Feedback> findByGameResultId(UUID gameResultId);
}
