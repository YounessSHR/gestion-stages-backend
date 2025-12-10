package com.gestionstages.model.entity;

import com.gestionstages.model.enums.StatutConventionEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "convention")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Convention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @CreationTimestamp
    @Column(name = "date_generation", updatable = false)
    private LocalDateTime dateGeneration;

    @Column(name = "date_debut_stage", nullable = false)
    private LocalDate dateDebutStage;

    @Column(name = "date_fin_stage", nullable = false)
    private LocalDate dateFinStage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutConventionEnum statut = StatutConventionEnum.BROUILLON;

    @Column(name = "signature_etudiant", nullable = false)
    private Boolean signatureEtudiant = false;

    @Column(name = "signature_entreprise", nullable = false)
    private Boolean signatureEntreprise = false;

    @Column(name = "signature_administration", nullable = false)
    private Boolean signatureAdministration = false;

    @Column(name = "fichier_pdf")
    private String fichierPdf;

    @OneToOne(mappedBy = "convention", cascade = CascadeType.ALL)
    private SuiviStage suiviStage;
}