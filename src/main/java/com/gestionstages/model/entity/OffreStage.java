package com.gestionstages.model.entity;

import com.gestionstages.model.enums.StatutOffreEnum;
import com.gestionstages.model.enums.TypeOffreEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offre_stage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffreStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_offre", nullable = false)
    private TypeOffreEnum typeOffre;

    @Column(nullable = false)
    private Integer duree; // en mois

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "competences_requises", columnDefinition = "TEXT")
    private String competencesRequises;

    @Column(precision = 10, scale = 2)
    private BigDecimal remuneration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutOffreEnum statut = StatutOffreEnum.EN_ATTENTE;

    @CreationTimestamp
    @Column(name = "date_publication", updatable = false)
    private LocalDateTime datePublication;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures = new ArrayList<>();
}