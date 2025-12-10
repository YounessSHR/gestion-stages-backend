package com.gestionstages.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "suivi_stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuiviStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La convention est obligatoire")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convention_id", nullable = false, unique = true)
    private Convention convention;

    @NotNull(message = "Le tuteur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuteur_id", nullable = false)
    private Tuteur tuteur;

    @NotNull(message = "Date d'affectation est obligatoire")
    @Column(nullable = false)
    private LocalDateTime dateAffectation;

    @NotBlank(message = "État d'avancement est obligatoire")
    @Column(nullable = false)
    private String etatAvancement;

    @Size(max = 5000, message = "Les commentaires ne peuvent pas dépasser 5000 caractères")
    @Column(columnDefinition = "TEXT")
    private String commentaires;

    private LocalDate derniereVisite;

    @PrePersist
    protected void onCreate() {
        dateAffectation = LocalDateTime.now();
        if (etatAvancement == null || etatAvancement.isEmpty()) {
            etatAvancement = "NON_COMMENCE";
        }
    }
}