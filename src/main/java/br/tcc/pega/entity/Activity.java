package br.tcc.pega.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "activities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ActivityType tipo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    /** Nível de dificuldade de 1 (fácil) a 5 (muito difícil) */
    @Column(nullable = false)
    private Integer dificuldade;

    @Column(nullable = false)
    private Boolean ativo;

    /** Sem cascade: excluir uma atividade não deve apagar o histórico de resultados */
    @OneToMany(mappedBy = "activity", fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<GameResult> resultados = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (this.ativo == null) this.ativo = true;
    }

    public enum ActivityType {
        LEITURA, ESCRITA, FONETICA, VOCABULARIO, SILABAS
    }
}
