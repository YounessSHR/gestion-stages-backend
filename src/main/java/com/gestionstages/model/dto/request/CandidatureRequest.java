package com.gestionstages.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatureRequest {

    @NotNull(message = "L'offre est obligatoire")
    private Long offreId;

    @NotBlank(message = "Lettre de motivation est obligatoire")
    @Size(min = 50, max = 5000, message = "La lettre de motivation doit contenir entre 50 et 5000 caractères")
    private String lettreMotivation;
}