package com.gestionstages.service.impl;

import com.gestionstages.exception.BadRequestException;
import com.gestionstages.exception.ResourceNotFoundException;
import com.gestionstages.exception.UnauthorizedException;
import com.gestionstages.model.dto.request.OffreRequest;
import com.gestionstages.model.dto.response.OffreResponse;
import com.gestionstages.model.entity.Entreprise;
import com.gestionstages.model.entity.OffreStage;
import com.gestionstages.model.enums.StatutOffreEnum;
import com.gestionstages.model.enums.TypeOffreEnum;
import com.gestionstages.repository.EntrepriseRepository;
import com.gestionstages.repository.OffreStageRepository;
import com.gestionstages.service.OffreService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing internship and work-study offers.
 * Handles CRUD operations, validation, and business rules (RG02).
 */
@Service
public class OffreServiceImpl implements OffreService {

    @Autowired
    private OffreStageRepository offreStageRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Retrieves all public offers (validated and not expired).
     * Only offers with status VALIDEE and expiration date in the future are returned.
     * 
     * @return List of public offer responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<OffreResponse> getAllOffresPubliques() {
        List<OffreStage> offres = offreStageRepository.findAllValidOffres();
        LocalDate now = LocalDate.now();
        
        // Additional filtering to ensure offers are not expired
        return offres.stream()
                .filter(o -> o.getStatut() == StatutOffreEnum.VALIDEE && 
                            (o.getDateExpiration() == null || o.getDateExpiration().isAfter(now)))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an offer by its ID.
     * Business Rule RG02: Only validated and non-expired offers are accessible publicly.
     * 
     * @param id The offer ID
     * @return Offer response
     * @throws ResourceNotFoundException if offer not found, not validated, or expired
     */
    @Override
    @Transactional(readOnly = true)
    public OffreResponse getOffreById(Long id) {
        OffreStage offre = offreStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée avec l'ID: " + id));
        
        // RG02: Only validated offers are accessible publicly
        if (offre.getStatut() != StatutOffreEnum.VALIDEE) {
            throw new ResourceNotFoundException("Offre non trouvée avec l'ID: " + id);
        }
        
        // Check if offer is expired
        LocalDate now = LocalDate.now();
        if (offre.getDateExpiration() != null && offre.getDateExpiration().isBefore(now)) {
            throw new ResourceNotFoundException("Offre non trouvée avec l'ID: " + id);
        }
        
        return convertToResponse(offre);
    }

    /**
     * Creates a new offer for an enterprise.
     * Business Rule RG02: New offers are created with status EN_ATTENTE (pending validation).
     * 
     * @param request The offer creation request
     * @param emailEntreprise The email of the enterprise creating the offer
     * @return Created offer response
     * @throws ResourceNotFoundException if enterprise not found
     * @throws BadRequestException if dates are invalid
     */
    @Override
    @Transactional
    public OffreResponse createOffre(OffreRequest request, String emailEntreprise) {
        // Retrieve the enterprise
        Entreprise entreprise = entrepriseRepository.findByEmail(emailEntreprise)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée"));

        // Validate dates
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new BadRequestException("La date de fin doit être après la date de début");
        }

        // Create the offer
        OffreStage offre = new OffreStage();
        offre.setTitre(request.getTitre());
        offre.setDescription(request.getDescription());
        
        // Convert typeOffre string to enum
        try {
            offre.setTypeOffre(TypeOffreEnum.valueOf(request.getTypeOffre().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Type d'offre invalide: " + request.getTypeOffre());
        }
        
        offre.setDuree(request.getDuree());
        offre.setDateDebut(request.getDateDebut());
        offre.setDateFin(request.getDateFin());
        offre.setCompetencesRequises(request.getCompetencesRequises());
        
        if (request.getRemuneration() != null) {
            offre.setRemuneration(BigDecimal.valueOf(request.getRemuneration()));
        }
        
        offre.setDateExpiration(request.getDateExpiration());
        offre.setEntreprise(entreprise);
        offre.setStatut(StatutOffreEnum.EN_ATTENTE); // RG02: Offer pending validation

        OffreStage savedOffre = offreStageRepository.save(offre);
        return convertToResponse(savedOffre);
    }

    /**
     * Updates an existing offer.
     * Only the owner enterprise can update their offers.
     * Validated offers cannot be modified.
     * 
     * @param id The offer ID
     * @param request The update request
     * @param emailEntreprise The email of the enterprise
     * @return Updated offer response
     * @throws UnauthorizedException if user is not the owner
     * @throws BadRequestException if offer is validated or dates are invalid
     */
    @Override
    @Transactional
    public OffreResponse updateOffre(Long id, OffreRequest request, String emailEntreprise) {
        OffreStage offre = offreStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée avec l'ID: " + id));

        // Verify that the user is the owner
        if (!offre.getEntreprise().getEmail().equals(emailEntreprise)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cette offre");
        }

        // Do not allow modification if offer is validated
        if (offre.getStatut() == StatutOffreEnum.VALIDEE) {
            throw new BadRequestException("Impossible de modifier une offre validée");
        }

        // Validate dates
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new BadRequestException("La date de fin doit être après la date de début");
        }

        // Update the offer
        offre.setTitre(request.getTitre());
        offre.setDescription(request.getDescription());
        
        try {
            offre.setTypeOffre(TypeOffreEnum.valueOf(request.getTypeOffre().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Type d'offre invalide: " + request.getTypeOffre());
        }
        
        offre.setDuree(request.getDuree());
        offre.setDateDebut(request.getDateDebut());
        offre.setDateFin(request.getDateFin());
        offre.setCompetencesRequises(request.getCompetencesRequises());
        
        if (request.getRemuneration() != null) {
            offre.setRemuneration(BigDecimal.valueOf(request.getRemuneration()));
        }
        
        offre.setDateExpiration(request.getDateExpiration());

        OffreStage updatedOffre = offreStageRepository.save(offre);
        return convertToResponse(updatedOffre);
    }

    /**
     * Deletes an offer.
     * Only the owner enterprise can delete their offers.
     * 
     * @param id The offer ID
     * @param emailEntreprise The email of the enterprise
     * @throws UnauthorizedException if user is not the owner
     */
    @Override
    @Transactional
    public void deleteOffre(Long id, String emailEntreprise) {
        OffreStage offre = offreStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée avec l'ID: " + id));

        // Verify that the user is the owner
        if (!offre.getEntreprise().getEmail().equals(emailEntreprise)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette offre");
        }

        offreStageRepository.delete(offre);
    }

    /**
     * Validates an offer (admin only).
     * Changes status from EN_ATTENTE to VALIDEE.
     * Only pending offers can be validated.
     * 
     * @param id The offer ID
     * @return Validated offer response
     * @throws BadRequestException if offer is not in pending status
     */
    @Override
    @Transactional
    public OffreResponse validerOffre(Long id) {
        OffreStage offre = offreStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre non trouvée avec l'ID: " + id));

        if (offre.getStatut() != StatutOffreEnum.EN_ATTENTE) {
            throw new BadRequestException("Seules les offres en attente peuvent être validées");
        }

        offre.setStatut(StatutOffreEnum.VALIDEE);
        OffreStage updatedOffre = offreStageRepository.save(offre);
        return convertToResponse(updatedOffre);
    }

    /**
     * Retrieves all offers for a specific enterprise.
     * 
     * @param emailEntreprise The enterprise email
     * @return List of offer responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<OffreResponse> getOffresByEntreprise(String emailEntreprise) {
        Entreprise entreprise = entrepriseRepository.findByEmail(emailEntreprise)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée avec l'email: " + emailEntreprise));

        List<OffreStage> offres = offreStageRepository.findByEntreprise(entreprise);
        return offres.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Searches offers by title.
     * Returns only validated and non-expired offers.
     * If search term is empty, returns all public offers.
     * 
     * @param titre The search term (title)
     * @return List of matching offer responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<OffreResponse> searchOffres(String titre) {
        // If search term is empty, return all public offers
        if (titre == null || titre.trim().isEmpty()) {
            return getAllOffresPubliques();
        }
        
        String searchTerm = titre.trim();
        List<OffreStage> offres = offreStageRepository.findByTitreContainingIgnoreCase(searchTerm);
        
        // Filter to keep only validated and non-expired offers
        LocalDate now = LocalDate.now();
        return offres.stream()
                .filter(o -> o.getStatut() == StatutOffreEnum.VALIDEE && 
                           (o.getDateExpiration() == null || o.getDateExpiration().isAfter(now)))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converts an OffreStage entity to OffreResponse DTO.
     * Maps entity fields to response DTO and includes related information.
     * 
     * @param offre The offer entity
     * @return Offer response DTO
     */
    private OffreResponse convertToResponse(OffreStage offre) {
        OffreResponse response = modelMapper.map(offre, OffreResponse.class);
        response.setTypeOffre(offre.getTypeOffre().name());
        response.setStatut(offre.getStatut().name());
        
        // Enterprise information
        if (offre.getEntreprise() != null) {
            response.setEntrepriseId(offre.getEntreprise().getId());
            response.setNomEntreprise(offre.getEntreprise().getNomEntreprise());
            response.setSecteurActivite(offre.getEntreprise().getSecteurActivite());
        }
        
        // Number of applications
        if (offre.getCandidatures() != null) {
            response.setNombreCandidatures((long) offre.getCandidatures().size());
        } else {
            response.setNombreCandidatures(0L);
        }
        
        // Convert remuneration from BigDecimal to Double
        if (offre.getRemuneration() != null) {
            response.setRemuneration(offre.getRemuneration().doubleValue());
        }
        
        return response;
    }
}
