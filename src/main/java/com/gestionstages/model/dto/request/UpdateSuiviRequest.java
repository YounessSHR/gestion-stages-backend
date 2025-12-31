package com.gestionstages.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSuiviRequest {

    private String etatAvancement;

    @Size(max = 5000, message = "Les commentaires ne peuvent pas dépasser 5000 caractères")
    private String commentaires;

    private LocalDate derniereVisite;
}

