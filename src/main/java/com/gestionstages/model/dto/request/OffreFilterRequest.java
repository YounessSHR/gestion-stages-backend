package com.gestionstages.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffreFilterRequest {
    private String search; // Recherche dans titre et description
    private String typeOffre; // STAGE ou ALTERNANCE
    private LocalDate dateDebutMin; // Date de début minimum
    private LocalDate dateDebutMax; // Date de début maximum
    private String sortBy; // datePublication, dateDebut, remuneration
    private String sortDirection; // ASC ou DESC
    private Integer page = 0;
    private Integer size = 10;
}
