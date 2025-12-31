package com.gestionstages.service.impl;

import com.gestionstages.model.dto.response.DashboardStatsResponse;
import com.gestionstages.model.entity.Entreprise;
import com.gestionstages.model.entity.OffreStage;
import com.gestionstages.model.entity.SuiviStage;
import com.gestionstages.model.entity.Tuteur;
import com.gestionstages.model.enums.EtatAvancementEnum;
import com.gestionstages.model.enums.StatutCandidatureEnum;
import com.gestionstages.model.enums.StatutConventionEnum;
import com.gestionstages.model.enums.StatutOffreEnum;
import com.gestionstages.repository.*;
import com.gestionstages.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for administration dashboard statistics.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OffreStageRepository offreStageRepository;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private ConventionRepository conventionRepository;

    @Autowired
    private SuiviStageRepository suiviStageRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private TuteurRepository tuteurRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        // Offres statistics
        List<OffreStage> allOffres = offreStageRepository.findAll();
        stats.setTotalOffres((long) allOffres.size());
        stats.setOffresEnAttente((long) offreStageRepository.findByStatut(StatutOffreEnum.EN_ATTENTE).size());
        stats.setOffresValidees((long) offreStageRepository.findByStatut(StatutOffreEnum.VALIDEE).size());
        LocalDate now = LocalDate.now();
        stats.setOffresExpirees((long) allOffres.stream()
                .filter(o -> o.getDateExpiration() != null && o.getDateExpiration().isBefore(now))
                .count());

        // Candidatures statistics
        List<com.gestionstages.model.entity.Candidature> allCandidatures = candidatureRepository.findAll();
        stats.setTotalCandidatures((long) allCandidatures.size());
        stats.setCandidaturesEnAttente((long) candidatureRepository.findByStatut(StatutCandidatureEnum.EN_ATTENTE).size());
        stats.setCandidaturesAcceptees((long) candidatureRepository.findByStatut(StatutCandidatureEnum.ACCEPTEE).size());
        stats.setCandidaturesRefusees((long) candidatureRepository.findByStatut(StatutCandidatureEnum.REFUSEE).size());

        // Conventions statistics
        List<com.gestionstages.model.entity.Convention> allConventions = conventionRepository.findAll();
        stats.setTotalConventions((long) allConventions.size());
        stats.setConventionsBrouillon((long) conventionRepository.findByStatut(StatutConventionEnum.BROUILLON).size());
        stats.setConventionsEnAttenteSignatures((long) conventionRepository.findByStatut(StatutConventionEnum.EN_ATTENTE_SIGNATURES).size());
        stats.setConventionsSignees((long) conventionRepository.findByStatut(StatutConventionEnum.SIGNEE).size());
        stats.setConventionsArchivees((long) conventionRepository.findByStatut(StatutConventionEnum.ARCHIVEE).size());

        // Suivi statistics
        List<SuiviStage> allSuivis = suiviStageRepository.findAll();
        stats.setTotalSuivis((long) allSuivis.size());
        stats.setStagesNonCommence((long) suiviStageRepository.findByEtatAvancement(EtatAvancementEnum.NON_COMMENCE).size());
        stats.setStagesEnCours((long) suiviStageRepository.findByEtatAvancement(EtatAvancementEnum.EN_COURS).size());
        stats.setStagesTermine((long) suiviStageRepository.findByEtatAvancement(EtatAvancementEnum.TERMINE).size());

        // Active students in internship (non-terminated)
        stats.setEtudiantsEnStage((long) allSuivis.stream()
                .filter(s -> s.getEtatAvancement() != EtatAvancementEnum.TERMINE)
                .map(s -> s.getConvention().getCandidature().getEtudiant().getId())
                .distinct()
                .count());

        // Active tutors (tutors with at least one active student)
        stats.setTuteursActifs((long) allSuivis.stream()
                .filter(s -> s.getEtatAvancement() != EtatAvancementEnum.TERMINE)
                .map(s -> s.getTuteur().getId())
                .distinct()
                .count());

        // Top entreprises (by number of offers)
        List<Entreprise> allEntreprises = entrepriseRepository.findAll();
        List<DashboardStatsResponse.TopEntreprise> topEntreprises = allEntreprises.stream()
                .map(entreprise -> {
                    long nbOffres = offreStageRepository.findByEntreprise(entreprise).size();
                    return new DashboardStatsResponse.TopEntreprise(
                            entreprise.getId(),
                            entreprise.getNomEntreprise(),
                            nbOffres
                    );
                })
                .filter(te -> te.getNombreOffres() > 0)
                .sorted((a, b) -> Long.compare(b.getNombreOffres(), a.getNombreOffres()))
                .limit(10)
                .collect(Collectors.toList());
        stats.setTopEntreprises(topEntreprises);

        // Top tuteurs (by number of active students)
        List<Tuteur> allTuteurs = tuteurRepository.findAll();
        List<DashboardStatsResponse.TopTuteur> topTuteurs = allTuteurs.stream()
                .map(tuteur -> {
                    long nbEtudiants = suiviStageRepository.findByTuteur(tuteur).stream()
                            .filter(s -> s.getEtatAvancement() != EtatAvancementEnum.TERMINE)
                            .count();
                    return new DashboardStatsResponse.TopTuteur(
                            tuteur.getId(),
                            tuteur.getNom(),
                            tuteur.getPrenom(),
                            nbEtudiants
                    );
                })
                .filter(tt -> tt.getNombreEtudiants() > 0)
                .sorted((a, b) -> Long.compare(b.getNombreEtudiants(), a.getNombreEtudiants()))
                .limit(10)
                .collect(Collectors.toList());
        stats.setTopTuteurs(topTuteurs);

        // Distribution by status - Offres
        Map<String, Long> distributionOffres = new HashMap<>();
        for (StatutOffreEnum statut : StatutOffreEnum.values()) {
            distributionOffres.put(statut.name(), (long) offreStageRepository.findByStatut(statut).size());
        }
        stats.setDistributionOffres(distributionOffres);

        // Distribution by status - Candidatures
        Map<String, Long> distributionCandidatures = new HashMap<>();
        for (StatutCandidatureEnum statut : StatutCandidatureEnum.values()) {
            distributionCandidatures.put(statut.name(), (long) candidatureRepository.findByStatut(statut).size());
        }
        stats.setDistributionCandidatures(distributionCandidatures);

        // Distribution by status - Conventions
        Map<String, Long> distributionConventions = new HashMap<>();
        for (StatutConventionEnum statut : StatutConventionEnum.values()) {
            distributionConventions.put(statut.name(), (long) conventionRepository.findByStatut(statut).size());
        }
        stats.setDistributionConventions(distributionConventions);

        // Distribution by status - Avancement
        Map<String, Long> distributionAvancement = new HashMap<>();
        for (EtatAvancementEnum etat : EtatAvancementEnum.values()) {
            distributionAvancement.put(etat.name(), (long) suiviStageRepository.findByEtatAvancement(etat).size());
        }
        stats.setDistributionAvancement(distributionAvancement);

        return stats;
    }
}

