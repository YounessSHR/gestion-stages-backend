package com.gestionstages.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "conventions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Convention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La candidature est obligatoire")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidature_id", nullable = false, unique = true)
    private Candidature candidature;

    @NotNull(message = "L'entreprise est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @NotNull(message = "Date de génération est obligatoire")
    @Column(nullable = false)
    private LocalDateTime dateGeneration;

    @NotNull(message = "Date de début de stage est obligatoire")
    @Future(message = "La date de début de stage doit être dans le futur")
    private LocalDate dateDebutStage;

    @NotNull(message = "Date de fin de stage est obligatoire")
    @Future(message = "La date de fin de stage doit être dans le futur")
    private LocalDate dateFinStage;

    @NotBlank(message = "Statut est obligatoire")
    @Column(nullable = false)
    private String statut;

    @NotNull(message = "Signature étudiant est obligatoire")
    @Column(nullable = false)
    private Boolean signatureEtudiant = false;

    @NotNull(message = "Signature entreprise est obligatoire")
    @Column(nullable = false)
    private Boolean signatureEntreprise = false;

    @NotNull(message = "Signature administration est obligatoire")
    @Column(nullable = false)
    private Boolean signatureAdministration = false;

    @Size(max = 255, message = "Le chemin du fichier PDF ne peut pas dépasser 255 caractères")
    private String fichierPdf;

    @OneToOne(mappedBy = "convention", cascade = CascadeType.ALL, orphanRemoval = true)
    private SuiviStage suiviStage;

    @PrePersist
    protected void onCreate() {
        dateGeneration = LocalDateTime.now();
        if (statut == null || statut.isEmpty()) {
            statut = "BROUILLON";
        }
        if (signatureEtudiant == null) {
            signatureEtudiant = false;
        }
        if (signatureEntreprise == null) {
            signatureEntreprise = false;
        }
        if (signatureAdministration == null) {
            signatureAdministration = false;
        }
    }
}