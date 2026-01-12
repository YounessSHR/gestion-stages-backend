package com.gestionstages.service.impl;

import com.gestionstages.exception.BadRequestException;
import com.gestionstages.exception.ResourceNotFoundException;
import com.gestionstages.exception.UnauthorizedException;
import com.gestionstages.model.dto.request.OffreFilterRequest;
import com.gestionstages.model.dto.request.OffreRequest;
import com.gestionstages.model.dto.response.OffreResponse;
import com.gestionstages.model.dto.response.PageResponse;
import com.gestionstages.model.entity.Entreprise;
import com.gestionstages.model.entity.OffreStage;
import com.gestionstages.model.enums.StatutOffreEnum;
import com.gestionstages.model.enums.TypeOffreEnum;
import com.gestionstages.repository.EntrepriseRepository;
import com.gestionstages.repository.OffreStageRepository;
import com.gestionstages.service.EmailService;
import com.gestionstages.service.NotificationService;
import com.gestionstages.service.OffreService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Retrieves public offers with pagination and filters.
     * 
     * @param filter Filter and pagination parameters
     * @return Paginated response with filtered offers
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<OffreResponse> getOffresPubliques(OffreFilterRequest filter) {
        // Prepare filter parameters
        String search = (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) 
                ? filter.getSearch().trim() : null;
        TypeOffreEnum typeOffre = null;
        if (filter.getTypeOffre() != null && !filter.getTypeOffre().trim().isEmpty()) {
            try {
                typeOffre = TypeOffreEnum.valueOf(filter.getTypeOffre().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid type, ignore
            }
        }
        
        // Prepare sorting
        Sort sort = Sort.by(Sort.Direction.DESC, "datePublication"); // Default sort
        if (filter.getSortBy() != null && !filter.getSortBy().trim().isEmpty()) {
            Sort.Direction direction = "ASC".equalsIgnoreCase(filter.getSortDirection()) 
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, filter.getSortBy());
        }
        
        // Prepare pagination
        int page = filter.getPage() != null && filter.getPage() >= 0 ? filter.getPage() : 0;
        int size = filter.getSize() != null && filter.getSize() > 0 && filter.getSize() <= 100 
                ? filter.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Query with filters
        Page<OffreStage> pageResult = offreStageRepository.findFilteredOffres(
                search, typeOffre, filter.getDateDebutMin(), filter.getDateDebutMax(), pageable
        );
        
        // Convert to response
        List<OffreResponse> content = pageResult.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isFirst(),
                pageResult.isLast()
        );
    }

    /**
     * Retrieves all public offers (validated and not expired).
     * Only offers with status VALIDEE and expiration date in the future are returned.
     * 
     * @return List of public offer responses
     * @deprecated Use getOffresPubliques(OffreFilterRequest) instead
     */
    @Override
    @Deprecated
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
        
        // Send email and notification asynchronously (non-blocking)
        emailService.sendOffreValidee(updatedOffre);
        notificationService.creerNotification(
                updatedOffre.getEntreprise().getId(),
                "Votre offre '" + updatedOffre.getTitre() + "' a été validée et est maintenant visible.",
                "OFFRE",
                "/entreprise/offres"
        );
        
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
     * Retrieves all offers (admin only).
     * Includes all offers regardless of status.
     * 
     * @return List of all offer responses
     */
    @Override
    @Transactional(readOnly = true)
    public List<OffreResponse> getAllOffres() {
        List<OffreStage> offres = offreStageRepository.findAll();
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

    /**
     * RG06: Marque automatiquement les offres expirées.
     * Une offre est considérée comme expirée si sa date d'expiration est passée.
     * Les offres expirées ne sont plus consultables publiquement.
     */
    @Override
    @Transactional
    public void marquerOffresExpirees() {
        LocalDate now = LocalDate.now();
        List<OffreStage> offresValidees = offreStageRepository.findByStatut(StatutOffreEnum.VALIDEE);
        
        int countExpired = 0;
        for (OffreStage offre : offresValidees) {
            if (offre.getDateExpiration() != null && offre.getDateExpiration().isBefore(now)) {
                offre.setStatut(StatutOffreEnum.EXPIREE);
                offreStageRepository.save(offre);
                countExpired++;
            }
        }
        
        if (countExpired > 0) {
            System.out.println("RG06: " + countExpired + " offre(s) marquée(s) comme expirée(s)");
        }
    }
}
