package com.gestionstages.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidatures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'étudiant est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @NotNull(message = "L'offre est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offre_id", nullable = false)
    private OffreStage offre;

    @NotBlank(message = "Lettre de motivation est obligatoire")
    @Size(min = 50, max = 5000, message = "La lettre de motivation doit contenir entre 50 et 5000 caractères")
    @Column(columnDefinition = "TEXT")
    private String lettreMotivation;

    @NotNull(message = "Date de candidature est obligatoire")
    @Column(nullable = false)
    private LocalDateTime dateCandidature;

    @NotBlank(message = "Statut est obligatoire")
    @Column(nullable = false)
    private String statut;

    @Size(max = 1000, message = "Le commentaire ne peut pas dépasser 1000 caractères")
    private String commentaire;

    @OneToOne(mappedBy = "candidature", cascade = CascadeType.ALL, orphanRemoval = true)
    private Convention convention;

    @PrePersist
    protected void onCreate() {
        dateCandidature = LocalDateTime.now();
        if (statut == null || statut.isEmpty()) {
            statut = "EN_ATTENTE";
        }
    }
}