package com.gestionstages.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConventionResponse {

    private Long id;
    private LocalDateTime dateGeneration;
    private LocalDate dateDebutStage;
    private LocalDate dateFinStage;
    private String statut;
    private Boolean signatureEtudiant;
    private Boolean signatureEntreprise;
    private Boolean signatureAdministration;
    private String fichierPdf;

    // Candidature information
    private Long candidatureId;
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantEmail;

    // Offre information
    private Long offreId;
    private String offreTitre;

    // Entreprise information
    private Long entrepriseId;
    private String entrepriseNom;

    // SuiviStage information (if exists)
    private Long suiviStageId;
    private Boolean hasSuiviStage;
}

