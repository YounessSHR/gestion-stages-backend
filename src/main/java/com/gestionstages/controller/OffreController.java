package com.gestionstages.controller;

import com.gestionstages.model.dto.request.OffreFilterRequest;
import com.gestionstages.model.dto.request.OffreRequest;
import com.gestionstages.model.dto.response.OffreResponse;
import com.gestionstages.model.dto.response.PageResponse;
import com.gestionstages.service.OffreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing internship and work-study offers.
 * Handles CRUD operations, validation, and search functionality.
 */
@RestController
@RequestMapping("/api/offres")
@CrossOrigin(origins = "*")
public class OffreController {

    @Autowired
    private OffreService offreService;

    /**
     * GET /api/offres/publiques
     * Retrieves public offers with pagination and filters.
     * Public endpoint - no authentication required.
     * 
     * Query parameters:
     * - search: Search term (title/description)
     * - typeOffre: STAGE or ALTERNANCE
     * - dateDebutMin: Minimum start date (YYYY-MM-DD)
     * - dateDebutMax: Maximum start date (YYYY-MM-DD)
     * - sortBy: Field to sort by (datePublication, dateDebut, remuneration)
     * - sortDirection: ASC or DESC
     * - page: Page number (default: 0)
     * - size: Page size (default: 10, max: 100)
     * 
     * @param filter Filter and pagination parameters
     * @return Paginated response with filtered offers
     */
    @GetMapping("/publiques")
    public ResponseEntity<PageResponse<OffreResponse>> getOffresPubliques(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String typeOffre,
            @RequestParam(required = false) String dateDebutMin,
            @RequestParam(required = false) String dateDebutMax,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        
        OffreFilterRequest filter = new OffreFilterRequest();
        filter.setSearch(search);
        filter.setTypeOffre(typeOffre);
        if (dateDebutMin != null && !dateDebutMin.isEmpty()) {
            try {
                filter.setDateDebutMin(java.time.LocalDate.parse(dateDebutMin));
            } catch (Exception e) {
                // Invalid date, ignore
            }
        }
        if (dateDebutMax != null && !dateDebutMax.isEmpty()) {
            try {
                filter.setDateDebutMax(java.time.LocalDate.parse(dateDebutMax));
            } catch (Exception e) {
                // Invalid date, ignore
            }
        }
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);
        filter.setPage(page);
        filter.setSize(size);
        
        PageResponse<OffreResponse> result = offreService.getOffresPubliques(filter);
        return ResponseEntity.ok(result);
    }
    
    /**
     * GET /api/offres/publiques/all
     * Retrieves all public offers without pagination (for backward compatibility).
     * @deprecated Use /publiques with pagination instead
     */
    @GetMapping("/publiques/all")
    @Deprecated
    public ResponseEntity<List<OffreResponse>> getAllOffresPubliques() {
        List<OffreResponse> offres = offreService.getAllOffresPubliques();
        return ResponseEntity.ok(offres);
    }

    /**
     * GET /api/offres/{id}
     * Retrieves an offer by its ID.
     * Public endpoint - only validated and non-expired offers are accessible.
     * 
     * @param id The offer ID
     * @return Offer response
     */
    @GetMapping("/{id}")
    public ResponseEntity<OffreResponse> getOffreById(@PathVariable Long id) {
        OffreResponse offre = offreService.getOffreById(id);
        return ResponseEntity.ok(offre);
    }

    /**
     * POST /api/offres
     * Creates a new offer.
     * Requires authentication - only enterprises can create offers.
     * 
     * @param request The offer creation request
     * @param authentication The authenticated user
     * @return Created offer response
     */
    @PostMapping
    public ResponseEntity<OffreResponse> createOffre(
            @Valid @RequestBody OffreRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        OffreResponse offre = offreService.createOffre(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(offre);
    }

    /**
     * PUT /api/offres/{id}
     * Updates an existing offer.
     * Requires authentication - only the owner enterprise can update.
     * 
     * @param id The offer ID
     * @param request The update request
     * @param authentication The authenticated user
     * @return Updated offer response
     */
    @PutMapping("/{id}")
    public ResponseEntity<OffreResponse> updateOffre(
            @PathVariable Long id,
            @Valid @RequestBody OffreRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        OffreResponse offre = offreService.updateOffre(id, request, email);
        return ResponseEntity.ok(offre);
    }

    /**
     * DELETE /api/offres/{id}
     * Deletes an offer.
     * Requires authentication - only the owner enterprise can delete.
     * 
     * @param id The offer ID
     * @param authentication The authenticated user
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffre(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        offreService.deleteOffre(id, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/offres/{id}/valider
     * Validates an offer.
     * Requires authentication - only administration can validate offers.
     * 
     * @param id The offer ID
     * @return Validated offer response
     */
    @PutMapping("/{id}/valider")
    public ResponseEntity<OffreResponse> validerOffre(@PathVariable Long id) {
        OffreResponse offre = offreService.validerOffre(id);
        return ResponseEntity.ok(offre);
    }

    /**
     * GET /api/offres/mes-offres
     * Retrieves all offers for the authenticated enterprise.
     * Requires authentication - returns offers of the logged-in enterprise.
     * 
     * @param authentication The authenticated user
     * @return List of offer responses
     */
    @GetMapping("/mes-offres")
    public ResponseEntity<List<OffreResponse>> getMesOffres(Authentication authentication) {
        String email = authentication.getName();
        List<OffreResponse> offres = offreService.getOffresByEntreprise(email);
        return ResponseEntity.ok(offres);
    }

    /**
     * GET /api/offres/search?titre=...
     * Searches offers by title.
     * Public endpoint - no authentication required.
     * If title parameter is empty, returns all public offers.
     * 
     * @param titre The search term (title)
     * @return List of matching offer responses
     */
    @GetMapping("/search")
    public ResponseEntity<List<OffreResponse>> searchOffres(
            @RequestParam(required = false, defaultValue = "") String titre) {
        // If title is empty, return all public offers
        if (titre == null || titre.trim().isEmpty()) {
            return ResponseEntity.ok(offreService.getAllOffresPubliques());
        }
        List<OffreResponse> offres = offreService.searchOffres(titre.trim());
        return ResponseEntity.ok(offres);
    }

    /**
     * GET /api/offres/all
     * Retrieves all offers (admin only).
     * Includes all offers regardless of status.
     * 
     * @return List of all offer responses
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public ResponseEntity<List<OffreResponse>> getAllOffres() {
        List<OffreResponse> offres = offreService.getAllOffres();
        return ResponseEntity.ok(offres);
    }

    /**
     * POST /api/offres/marquer-expirees
     * Marks expired offers as expired (RG06).
     * Requires authentication - admin only.
     * This can also be called automatically by a scheduler.
     * 
     * @return Success message
     */
    @PostMapping("/marquer-expirees")
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public ResponseEntity<?> marquerOffresExpirees() {
        offreService.marquerOffresExpirees();
        return ResponseEntity.ok().body(new java.util.HashMap<String, String>() {{
            put("message", "Offres expirées marquées avec succès (RG06)");
        }});
    }
}
