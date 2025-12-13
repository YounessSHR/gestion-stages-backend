package com.gestionstages.service.impl;

import com.gestionstages.exception.BadRequestException;
import com.gestionstages.exception.ResourceNotFoundException;
import com.gestionstages.exception.UnauthorizedException;
import com.gestionstages.model.dto.request.CandidatureRequest;
import com.gestionstages.model.dto.response.CandidatureResponse;
import com.gestionstages.model.entity.Candidature;
import com.gestionstages.model.entity.Convention;
import com.gestionstages.model.entity.Etudiant;
import com.gestionstages.model.entity.OffreStage;
import com.gestionstages.model.entity.Utilisateur;
import com.gestionstages.model.enums.RoleEnum;
import com.gestionstages.model.enums.StatutCandidatureEnum;
import com.gestionstages.model.enums.StatutConventionEnum;
import com.gestionstages.model.enums.StatutOffreEnum;
import com.gestionstages.repository.CandidatureRepository;
import com.gestionstages.repository.ConventionRepository;
import com.gestionstages.repository.EtudiantRepository;
import com.gestionstages.repository.OffreStageRepository;
import com.gestionstages.repository.UtilisateurRepository;
import com.gestionstages.service.CandidatureService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing applications (candidatures).
 * Handles application creation, acceptance/rejection, and business rules (RG01, RG03).
 */
@Service
public class CandidatureServiceImpl implements CandidatureService {

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private OffreStageRepository offreStageRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ConventionRepository conventionRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Creates a new application for an offer.
     * Business Rule RG01: A student can only apply once to the same offer.
     * Business Rule RG02: Only validated and non-expired offers can receive applications.
     * 
     * @param request The application request
     * @param emailEtudiant The email of the student applying
     * @return Created application response
     * @throws UnauthorizedException if user is not a student
     * @throws BadRequestException if offer is not validated, expired, or student already applied
     */
    @Override
    @Transactional
    public CandidatureResponse createCandidature(CandidatureRequest request, String emailEtudiant) {
        // Verify that the user is a student
        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailEtudiant)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        if (utilisateur.getRole() != RoleEnum.ETUDIANT) {
            throw new UnauthorizedException("Seuls les étudiants peuvent postuler");
        }

        Etudiant etudiant = etudiantRepository.findById(utilisateur.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        // Retrieve the offer
        OffreStage offre = offreStageRepository.findById(request.getOffreId())
                .orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée avec l'ID: " + request.getOffreId()));

        // RG02: Verify that the offer is validated and not expired
        if (offre.getStatut() != StatutOffreEnum.VALIDEE) {
            throw new BadRequestException("Cette offre n'est pas encore validée");
        }

        if (offre.getDateExpiration() != null && offre.getDateExpiration().isBefore(java.time.LocalDate.now())) {
            throw new BadRequestException("Cette offre est expirée");
        }

        // RG01: A student can only apply once to the same offer
        if (candidatureRepository.existsByEtudiantAndOffre(etudiant, offre)) {
            throw new BadRequestException("Vous avez déjà postulé à cette offre");
        }

        // Create the application
        Candidature candidature = new Candidature();
        candidature.setEtudiant(etudiant);
        candidature.setOffre(offre);
        candidature.setLettreMotivation(request.getLettreMotivation());
        candidature.setStatut(StatutCandidatureEnum.EN_ATTENTE);

        Candidature savedCandidature = candidatureRepository.save(candidature);
        return convertToResponse(savedCandidature);
    }

    /**
     * Retrieves an application by its ID.
     * 
     * @param id The application ID
     * @return Application response
     */
    @Override
    @Transactional(readOnly = true)
    public CandidatureResponse getCandidatureById(Long id) {
        Candidature candidature = candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature non trouvée avec l'ID: " + id));
        return convertToResponse(candidature);
    }

    /**
     * Retrieves all applications for a specific student.
     * 
     * @param emailEtudiant The student email
     * @return List of application responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<CandidatureResponse> getCandidaturesByEtudiant(String emailEtudiant) {
        Etudiant etudiant = etudiantRepository.findByEmail(emailEtudiant)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        List<Candidature> candidatures = candidatureRepository.findByEtudiant(etudiant);
        return candidatures.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all applications for a specific offer.
     * Only the owner enterprise can view applications for their offers.
     * 
     * @param offreId The offer ID
     * @param emailEntreprise The enterprise email
     * @return List of application responses
     * @throws UnauthorizedException if user is not the offer owner
     */
    @Override
    @Transactional(readOnly = true)
    public List<CandidatureResponse> getCandidaturesByOffre(Long offreId, String emailEntreprise) {
        OffreStage offre = offreStageRepository.findById(offreId)
                .orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée avec l'ID: " + offreId));

        // Verify that the user is the offer owner
        if (!offre.getEntreprise().getEmail().equals(emailEntreprise)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à consulter les candidatures de cette offre");
        }

        List<Candidature> candidatures = candidatureRepository.findByOffre(offre);
        return candidatures.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Accepts an application.
     * Business Rule RG03: An accepted application automatically triggers convention generation.
     * Only the offer owner can accept applications.
     * 
     * @param id The application ID
     * @param emailEntreprise The enterprise email
     * @return Accepted application response
     * @throws UnauthorizedException if user is not the offer owner
     * @throws BadRequestException if application is already processed
     */
    @Override
    @Transactional
    public CandidatureResponse accepterCandidature(Long id, String emailEntreprise) {
        Candidature candidature = candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature non trouvée avec l'ID: " + id));

        // Verify that the user is the offer owner
        if (!candidature.getOffre().getEntreprise().getEmail().equals(emailEntreprise)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à accepter cette candidature");
        }

        // Verify that the application is pending
        if (candidature.getStatut() != StatutCandidatureEnum.EN_ATTENTE) {
            throw new BadRequestException("Cette candidature a déjà été traitée");
        }

        // Accept the application
        candidature.setStatut(StatutCandidatureEnum.ACCEPTEE);
        candidature.setDateTraitement(LocalDateTime.now());

        Candidature savedCandidature = candidatureRepository.save(candidature);

        // RG03: An accepted application triggers automatic convention generation
        if (savedCandidature.getConvention() == null) {
            Convention convention = new Convention();
            convention.setCandidature(savedCandidature);
            convention.setDateDebutStage(savedCandidature.getOffre().getDateDebut());
            convention.setDateFinStage(savedCandidature.getOffre().getDateFin());
            convention.setStatut(StatutConventionEnum.BROUILLON);
            convention.setSignatureEtudiant(false);
            convention.setSignatureEntreprise(false);
            convention.setSignatureAdministration(false);
            
            conventionRepository.save(convention);
        }

        return convertToResponse(savedCandidature);
    }

    /**
     * Rejects an application.
     * Only the offer owner can reject applications.
     * 
     * @param id The application ID
     * @param emailEntreprise The enterprise email
     * @param commentaire Optional rejection comment
     * @return Rejected application response
     * @throws UnauthorizedException if user is not the offer owner
     * @throws BadRequestException if application is already processed
     */
    @Override
    @Transactional
    public CandidatureResponse refuserCandidature(Long id, String emailEntreprise, String commentaire) {
        Candidature candidature = candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature non trouvée avec l'ID: " + id));

        // Verify that the user is the offer owner
        if (!candidature.getOffre().getEntreprise().getEmail().equals(emailEntreprise)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à refuser cette candidature");
        }

        // Verify that the application is pending
        if (candidature.getStatut() != StatutCandidatureEnum.EN_ATTENTE) {
            throw new BadRequestException("Cette candidature a déjà été traitée");
        }

        // Reject the application
        candidature.setStatut(StatutCandidatureEnum.REFUSEE);
        candidature.setCommentaire(commentaire);
        candidature.setDateTraitement(LocalDateTime.now());

        Candidature savedCandidature = candidatureRepository.save(candidature);
        return convertToResponse(savedCandidature);
    }

    /**
     * Deletes an application.
     * Only the student owner can delete their applications.
     * Accepted applications cannot be deleted.
     * 
     * @param id The application ID
     * @param emailEtudiant The student email
     * @throws UnauthorizedException if user is not the application owner
     * @throws BadRequestException if application is accepted
     */
    @Override
    @Transactional
    public void deleteCandidature(Long id, String emailEtudiant) {
        Candidature candidature = candidatureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature non trouvée avec l'ID: " + id));

        // Verify that the user is the application owner
        if (!candidature.getEtudiant().getEmail().equals(emailEtudiant)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette candidature");
        }

        // Do not allow deletion if application is accepted
        if (candidature.getStatut() == StatutCandidatureEnum.ACCEPTEE) {
            throw new BadRequestException("Impossible de supprimer une candidature acceptée");
        }

        candidatureRepository.delete(candidature);
    }

    /**
     * Converts a Candidature entity to CandidatureResponse DTO.
     * Maps entity fields to response DTO and includes related information (student, offer, enterprise).
     * 
     * @param candidature The application entity
     * @return Application response DTO
     */
    private CandidatureResponse convertToResponse(Candidature candidature) {
        CandidatureResponse response = modelMapper.map(candidature, CandidatureResponse.class);
        response.setStatut(candidature.getStatut().name());

        // Student information
        if (candidature.getEtudiant() != null) {
            response.setEtudiantId(candidature.getEtudiant().getId());
            response.setEtudiantNom(candidature.getEtudiant().getNom());
            response.setEtudiantPrenom(candidature.getEtudiant().getPrenom());
            response.setEtudiantEmail(candidature.getEtudiant().getEmail());
            response.setEtudiantNiveau(candidature.getEtudiant().getNiveau());
            response.setEtudiantFiliere(candidature.getEtudiant().getFiliere());
        }

        // Offer information
        if (candidature.getOffre() != null) {
            response.setOffreId(candidature.getOffre().getId());
            response.setOffreTitre(candidature.getOffre().getTitre());
            response.setOffreTypeOffre(candidature.getOffre().getTypeOffre().name());

            // Enterprise information
            if (candidature.getOffre().getEntreprise() != null) {
                response.setEntrepriseId(candidature.getOffre().getEntreprise().getId());
                response.setEntrepriseNom(candidature.getOffre().getEntreprise().getNomEntreprise());
            }
        }

        return response;
    }
}
