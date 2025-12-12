package com.gestionstages.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffreRequest {

    @NotBlank(message = "Titre est obligatoire")
    @Size(min = 5, max = 200)
    private String titre;

    @NotBlank(message = "Description est obligatoire")
    @Size(min = 20, max = 10000)
    private String description;

    @NotBlank(message = "Type d'offre est obligatoire")
    private String typeOffre;

    @Min(value = 1, message = "La durée doit être au moins de 1 mois")
    @Max(value = 24, message = "La durée ne peut pas dépasser 24 mois")
    private Integer duree;

    @Future(message = "La date de début doit être dans le futur")
    private LocalDate dateDebut;

    @Future(message = "La date de fin doit être dans le futur")
    private LocalDate dateFin;

    private String competencesRequises;

    @Min(value = 0, message = "La rémunération doit être positive")
    private Double remuneration;

    private LocalDate dateExpiration;
}