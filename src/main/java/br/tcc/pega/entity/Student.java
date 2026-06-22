package br.tcc.pega.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_students_user_id", columnList = "user_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Professor ou terapeuta responsável por este aluno — não é o aluno como usuário da plataforma */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User responsavel;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "nivel_atual", nullable = false)
    private Integer nivelAtual;

    @Column(name = "score_total", nullable = false)
    private Integer scoreTotal;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Cascateia remoção: ao excluir o aluno, seus resultados (e feedbacks vinculados) são excluídos */
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<GameResult> resultados = new ArrayList<>();

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.nivelAtual == null) this.nivelAtual = 1;
        if (this.scoreTotal == null) this.scoreTotal = 0;
    }
}
