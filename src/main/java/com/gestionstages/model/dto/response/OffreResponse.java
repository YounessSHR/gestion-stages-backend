package com.gestionstages.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffreResponse {

    private Long id;
    private String titre;
    private String description;
    private String typeOffre;
    private Integer duree;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String competencesRequises;
    private Double remuneration;
    private String statut;
    private LocalDateTime datePublication;
    private LocalDate dateExpiration;

    // Info entreprise
    private Long entrepriseId;
    private String nomEntreprise;
    private String secteurActivite;

    // Stats
    private Long nombreCandidatures;
}