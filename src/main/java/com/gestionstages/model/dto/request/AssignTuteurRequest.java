package com.gestionstages.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTuteurRequest {

    @NotNull(message = "L'ID de la convention est obligatoire")
    private Long conventionId;

    @NotNull(message = "L'ID du tuteur est obligatoire")
    private Long tuteurId;
}

