package com.gestionstages.model.entity;

import com.gestionstages.model.enums.StatutCandidatureEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidature", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"etudiant_id", "offre_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offre_id", nullable = false)
    private OffreStage offre;

    @Column(name = "lettre_motivation", columnDefinition = "TEXT")
    private String lettreMotivation;

    @CreationTimestamp
    @Column(name = "date_candidature", updatable = false)
    private LocalDateTime dateCandidature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidatureEnum statut = StatutCandidatureEnum.EN_ATTENTE;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @OneToOne(mappedBy = "candidature", cascade = CascadeType.ALL)
    private Convention convention;
}