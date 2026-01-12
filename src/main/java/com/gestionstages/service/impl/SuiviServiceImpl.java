package com.gestionstages.service.impl;

import com.gestionstages.exception.BadRequestException;
import com.gestionstages.exception.ResourceNotFoundException;
import com.gestionstages.exception.UnauthorizedException;
import com.gestionstages.model.dto.request.AssignTuteurRequest;
import com.gestionstages.model.dto.request.UpdateSuiviRequest;
import com.gestionstages.model.dto.response.SuiviStageResponse;
import com.gestionstages.model.entity.Convention;
import com.gestionstages.model.entity.SuiviStage;
import com.gestionstages.model.entity.Tuteur;
import com.gestionstages.model.enums.EtatAvancementEnum;
import com.gestionstages.model.enums.RoleEnum;
import com.gestionstages.model.enums.StatutConventionEnum;
import com.gestionstages.repository.ConventionRepository;
import com.gestionstages.repository.SuiviStageRepository;
import com.gestionstages.repository.TuteurRepository;
import com.gestionstages.service.EmailService;
import com.gestionstages.service.SuiviService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing stage follow-up (suivi stages).
 * Handles tutor assignments, progress tracking, and business rules (RG05, RG07).
 */
@Service
public class SuiviServiceImpl implements SuiviService {

    private static final int MAX_STUDENTS_PER_TUTOR = 10;

    @Autowired
    private SuiviStageRepository suiviStageRepository;

    @Autowired
    private ConventionRepository conventionRepository;

    @Autowired
    private TuteurRepository tuteurRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public SuiviStageResponse assignerTuteur(AssignTuteurRequest request) {
        // Retrieve convention
        Convention convention = conventionRepository.findById(request.getConventionId())
                .orElseThrow(() -> new ResourceNotFoundException("Convention non trouvée avec l'ID: " + request.getConventionId()));

        // Verify convention is signed
        if (convention.getStatut() != StatutConventionEnum.SIGNEE) {
            throw new BadRequestException("Seules les conventions signées peuvent recevoir un tuteur");
        }

        // Check if convention already has a suivi stage
        if (convention.getSuiviStage() != null) {
            throw new BadRequestException("Cette convention a déjà un tuteur assigné");
        }

        // Retrieve tutor - using findById with JOINED inheritance
        // The ID is in the utilisateur table, and tuteur extends utilisateur
        Optional<Tuteur> tuteurOpt = tuteurRepository.findById(request.getTuteurId());
        if (tuteurOpt.isEmpty()) {
            throw new ResourceNotFoundException("Tuteur non trouvé avec l'ID: " + request.getTuteurId());
        }
        Tuteur tuteur = tuteurOpt.get();
        
        // Verify that the user is actually a tutor (has TUTEUR role)
        if (tuteur.getRole() != RoleEnum.TUTEUR) {
            throw new BadRequestException("L'utilisateur avec l'ID " + request.getTuteurId() + " n'est pas un tuteur");
        }

        // RG05: A tutor can follow max 10 students (active ones only)
        Long countActiveStudents = suiviStageRepository.countByTuteurEmailAndEtatAvancementNotTermine(tuteur.getEmail());
        if (countActiveStudents >= MAX_STUDENTS_PER_TUTOR) {
            throw new BadRequestException("Ce tuteur a atteint la limite de " + MAX_STUDENTS_PER_TUTOR + " étudiants actifs");
        }

        // RG07: A student can only have one active internship at a time
        String etudiantEmail = convention.getCandidature().getEtudiant().getEmail();
        List<SuiviStage> activeStages = suiviStageRepository.findActiveByEtudiantEmail(etudiantEmail);
        if (!activeStages.isEmpty()) {
            throw new BadRequestException("Cet étudiant a déjà un stage actif");
        }

        // Create suivi stage
        SuiviStage suiviStage = new SuiviStage();
        suiviStage.setConvention(convention);
        suiviStage.setTuteur(tuteur);
        suiviStage.setEtatAvancement(EtatAvancementEnum.NON_COMMENCE);

        SuiviStage savedSuiviStage = suiviStageRepository.save(suiviStage);
        
        // Send email notification asynchronously (non-blocking)
        emailService.sendTuteurAssigne(savedSuiviStage);
        
        return convertToResponse(savedSuiviStage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuiviStageResponse> getAllSuivis() {
        List<SuiviStage> suivis = suiviStageRepository.findAll();
        return suivis.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SuiviStageResponse getSuiviById(Long id) {
        SuiviStage suiviStage = suiviStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suivi non trouvé avec l'ID: " + id));
        return convertToResponse(suiviStage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuiviStageResponse> getMesEtudiants(String emailTuteur) {
        List<SuiviStage> suivis = suiviStageRepository.findByTuteurEmail(emailTuteur);
        return suivis.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SuiviStageResponse getMonStage(String emailEtudiant) {
        List<SuiviStage> activeStages = suiviStageRepository.findActiveByEtudiantEmail(emailEtudiant);
        if (activeStages.isEmpty()) {
            // Return null or throw exception? For now, return null and handle in controller
            return null;
        }
        // Return the most recent active stage (should be only one due to RG07)
        return convertToResponse(activeStages.get(0));
    }

    @Override
    @Transactional
    public SuiviStageResponse updateSuivi(Long id, UpdateSuiviRequest request, String emailTuteur) {
        SuiviStage suiviStage = suiviStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Suivi non trouvé avec l'ID: " + id));

        // Verify that the user is the tutor
        if (!suiviStage.getTuteur().getEmail().equals(emailTuteur)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce suivi");
        }

        // Update etat avancement if provided
        if (request.getEtatAvancement() != null && !request.getEtatAvancement().trim().isEmpty()) {
            try {
                EtatAvancementEnum etat = EtatAvancementEnum.valueOf(request.getEtatAvancement().toUpperCase());
                suiviStage.setEtatAvancement(etat);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("État d'avancement invalide: " + request.getEtatAvancement());
            }
        }

        // Update commentaires if provided
        if (request.getCommentaires() != null) {
            suiviStage.setCommentaires(request.getCommentaires());
        }

        // Update derniere visite if provided
        if (request.getDerniereVisite() != null) {
            suiviStage.setDerniereVisite(request.getDerniereVisite());
        }

        SuiviStage savedSuiviStage = suiviStageRepository.save(suiviStage);
        return convertToResponse(savedSuiviStage);
    }

    /**
     * Converts a SuiviStage entity to SuiviStageResponse DTO.
     * Maps entity fields to response DTO and includes related information.
     * 
     * @param suiviStage The suivi stage entity
     * @return Suivi stage response DTO
     */
    private SuiviStageResponse convertToResponse(SuiviStage suiviStage) {
        SuiviStageResponse response = modelMapper.map(suiviStage, SuiviStageResponse.class);
        response.setEtatAvancement(suiviStage.getEtatAvancement().name());

        // Convention information
        if (suiviStage.getConvention() != null) {
            response.setConventionId(suiviStage.getConvention().getId());
            response.setDateDebutStage(suiviStage.getConvention().getDateDebutStage());
            response.setDateFinStage(suiviStage.getConvention().getDateFinStage());

            // Student information (from convention -> candidature -> etudiant)
            if (suiviStage.getConvention().getCandidature() != null &&
                suiviStage.getConvention().getCandidature().getEtudiant() != null) {
                response.setEtudiantId(suiviStage.getConvention().getCandidature().getEtudiant().getId());
                response.setEtudiantNom(suiviStage.getConvention().getCandidature().getEtudiant().getNom());
                response.setEtudiantPrenom(suiviStage.getConvention().getCandidature().getEtudiant().getPrenom());
                response.setEtudiantEmail(suiviStage.getConvention().getCandidature().getEtudiant().getEmail());
                response.setEtudiantNiveau(suiviStage.getConvention().getCandidature().getEtudiant().getNiveau());
                response.setEtudiantFiliere(suiviStage.getConvention().getCandidature().getEtudiant().getFiliere());
            }

            // Offre information
            if (suiviStage.getConvention().getCandidature() != null &&
                suiviStage.getConvention().getCandidature().getOffre() != null) {
                response.setOffreId(suiviStage.getConvention().getCandidature().getOffre().getId());
                response.setOffreTitre(suiviStage.getConvention().getCandidature().getOffre().getTitre());
            }
        }

        // Tutor information
        if (suiviStage.getTuteur() != null) {
            response.setTuteurId(suiviStage.getTuteur().getId());
            response.setTuteurNom(suiviStage.getTuteur().getNom());
            response.setTuteurPrenom(suiviStage.getTuteur().getPrenom());
            response.setTuteurEmail(suiviStage.getTuteur().getEmail());
            response.setTuteurDepartement(suiviStage.getTuteur().getDepartement());
            response.setTuteurSpecialite(suiviStage.getTuteur().getSpecialite());
        }

        return response;
    }
}
