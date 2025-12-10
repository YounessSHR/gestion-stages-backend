package com.gestionstages.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offres_stage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffreStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titre est obligatoire")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    @Column(nullable = false)
    private String titre;

    @NotBlank(message = "Description est obligatoire")
    @Size(min = 20, max = 10000, message = "La description doit contenir entre 20 et 10000 caractères")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotBlank(message = "Type d'offre est obligatoire")
    @Column(nullable = false)
    private String typeOffre;

    @Min(value = 1, message = "La durée doit être au moins de 1 mois")
    @Max(value = 24, message = "La durée ne peut pas dépasser 24 mois")
    private Integer duree;

    @Future(message = "La date de début doit être dans le futur")
    private LocalDate dateDebut;

    @Future(message = "La date de fin doit être dans le futur")
    private LocalDate dateFin;

    @Size(max = 1000, message = "Les compétences requises ne peuvent pas dépasser 1000 caractères")
    private String competencesRequises;

    @Min(value = 0, message = "La rémunération doit être positive")
    private Double remuneration;

    @NotNull(message = "L'entreprise est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @NotBlank(message = "Statut est obligatoire")
    @Column(nullable = false)
    private String statut;

    private LocalDateTime datePublication;

    private LocalDate dateExpiration;

    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (statut == null || statut.isEmpty()) {
            statut = "BROUILLON";
        }
    }
}