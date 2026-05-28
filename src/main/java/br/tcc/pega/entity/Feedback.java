package br.tcc.pega.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Feedback gerado após cada sessão de jogo.
 * Será populado futuramente pelo agente JADE de avaliação (AssessmentAgent).
 */
@Entity
@Table(name = "feedbacks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_result_id", nullable = false, unique = true)
    private GameResult gameResult;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeedbackType tipo;

    public enum FeedbackType {
        POSITIVO, NEUTRO, NEGATIVO, ENCORAJAMENTO
    }
}
