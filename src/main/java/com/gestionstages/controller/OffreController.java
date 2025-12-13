package com.gestionstages.controller;

import com.gestionstages.model.dto.request.OffreRequest;
import com.gestionstages.model.dto.response.OffreResponse;
import com.gestionstages.service.OffreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Retrieves all public offers (validated and non-expired).
     * Public endpoint - no authentication required.
     * 
     * @return List of public offer responses
     */
    @GetMapping("/publiques")
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
}
