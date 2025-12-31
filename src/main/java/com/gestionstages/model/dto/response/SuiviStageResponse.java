package com.gestionstages.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuiviStageResponse {

    private Long id;
    private LocalDateTime dateAffectation;
    private String etatAvancement;
    private String commentaires;
    private LocalDate derniereVisite;

    // Convention information
    private Long conventionId;
    private LocalDate dateDebutStage;
    private LocalDate dateFinStage;

    // Tuteur information
    private Long tuteurId;
    private String tuteurNom;
    private String tuteurPrenom;
    private String tuteurEmail;
    private String tuteurDepartement;
    private String tuteurSpecialite;

    // Etudiant information
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantEmail;
    private String etudiantNiveau;
    private String etudiantFiliere;

    // Offre information
    private Long offreId;
    private String offreTitre;
}

