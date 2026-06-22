package br.tcc.pega.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "game_results", indexes = {
        @Index(name = "idx_game_results_student_id",  columnList = "student_id"),
        @Index(name = "idx_game_results_activity_id", columnList = "activity_id"),
        @Index(name = "idx_game_results_timestamp",   columnList = "timestamp")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(nullable = false)
    private Integer acertos;

    @Column(nullable = false)
    private Integer erros;

    /** Duração total da sessão em milissegundos */
    @Column(name = "tempo_ms", nullable = false)
    private Long tempoMs;

    /** JSON com detalhamento por palavra: [{palavra, acertou, tempoMs}, ...] */
    @Column(name = "detalhes_palavras", columnDefinition = "TEXT")
    private String detalhesPalavras;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    /** Cascateia remoção: ao excluir o resultado, o feedback vinculado é excluído */
    @OneToOne(mappedBy = "gameResult", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Feedback feedback;

    @PrePersist
    void prePersist() {
        if (this.timestamp == null) this.timestamp = LocalDateTime.now();
    }
}
