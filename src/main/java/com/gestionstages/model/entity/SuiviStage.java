package com.gestionstages.model.entity;

import com.gestionstages.model.enums.EtatAvancementEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "suivi_stage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuiviStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "convention_id", nullable = false, unique = true)
    private Convention convention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuteur_id", nullable = false)
    private Tuteur tuteur;

    @CreationTimestamp
    @Column(name = "date_affectation", updatable = false)
    private LocalDateTime dateAffectation;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat_avancement", nullable = false)
    private EtatAvancementEnum etatAvancement = EtatAvancementEnum.NON_COMMENCE;

    @Column(columnDefinition = "TEXT")
    private String commentaires;

    @Column(name = "derniere_visite")
    private LocalDate derniereVisite;
}