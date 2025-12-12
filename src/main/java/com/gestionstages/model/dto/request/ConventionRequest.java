package com.gestionstages.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConventionRequest {

    @NotNull(message = "La candidature est obligatoire")
    private Long candidatureId;

    @NotNull(message = "Date de début de stage est obligatoire")
    @Future(message = "La date de début de stage doit être dans le futur")
    private LocalDate dateDebutStage;

    @NotNull(message = "Date de fin de stage est obligatoire")
    @Future(message = "La date de fin de stage doit être dans le futur")
    private LocalDate dateFinStage;
}