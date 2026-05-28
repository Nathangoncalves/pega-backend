package br.tcc.pega.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Professor ou terapeuta responsável por este aluno */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "nivel_atual", nullable = false)
    private Integer nivelAtual;

    @Column(name = "score_total", nullable = false)
    private Integer scoreTotal;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.nivelAtual == null) this.nivelAtual = 1;
        if (this.scoreTotal == null) this.scoreTotal = 0;
    }
}
