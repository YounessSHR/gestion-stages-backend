package com.gestionstages.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidatureResponse {

    private Long id;
    private String lettreMotivation;
    private LocalDateTime dateCandidature;
    private String statut;
    private String commentaire;

    // Info étudiant
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantEmail;
    private String etudiantNiveau;
    private String etudiantFiliere;

    // Info offre
    private Long offreId;
    private String offreTitre;
    private String offreTypeOffre;

    // Info entreprise
    private Long entrepriseId;
    private String entrepriseNom;
}