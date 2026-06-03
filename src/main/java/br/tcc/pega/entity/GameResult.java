package br.tcc.pega.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_results")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @PrePersist
    void prePersist() {
        if (this.timestamp == null) this.timestamp = LocalDateTime.now();
    }
}
