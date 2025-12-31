package com.gestionstages.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    // General statistics
    private Long totalOffres;
    private Long offresEnAttente;
    private Long offresValidees;
    private Long offresExpirees;

    private Long totalCandidatures;
    private Long candidaturesEnAttente;
    private Long candidaturesAcceptees;
    private Long candidaturesRefusees;

    private Long totalConventions;
    private Long conventionsBrouillon;
    private Long conventionsEnAttenteSignatures;
    private Long conventionsSignees;
    private Long conventionsArchivees;

    private Long totalSuivis;
    private Long stagesNonCommence;
    private Long stagesEnCours;
    private Long stagesTermine;

    private Long etudiantsEnStage;
    private Long tuteursActifs;

    // Top entities
    private List<TopEntreprise> topEntreprises;
    private List<TopTuteur> topTuteurs;

    // Distribution by status
    private Map<String, Long> distributionOffres;
    private Map<String, Long> distributionCandidatures;
    private Map<String, Long> distributionConventions;
    private Map<String, Long> distributionAvancement;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopEntreprise {
        private Long entrepriseId;
        private String nomEntreprise;
        private Long nombreOffres;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopTuteur {
        private Long tuteurId;
        private String tuteurNom;
        private String tuteurPrenom;
        private Long nombreEtudiants;
    }
}

