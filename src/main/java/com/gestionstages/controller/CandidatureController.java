package com.gestionstages.controller;

import com.gestionstages.model.dto.request.CandidatureRequest;
import com.gestionstages.model.dto.response.CandidatureResponse;
import com.gestionstages.service.CandidatureService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing applications (candidatures).
 * Handles application creation, acceptance/rejection, and retrieval operations.
 */
@RestController
@RequestMapping("/api/candidatures")
@CrossOrigin(origins = "*")
public class CandidatureController {

    @Autowired
    private CandidatureService candidatureService;

    /**
     * POST /api/candidatures
     * Creates a new application for an offer.
     * Requires authentication - only students can create applications.
     * 
     * @param request The application request
     * @param authentication The authenticated user
     * @return Created application response
     */
    @PostMapping
    public ResponseEntity<CandidatureResponse> createCandidature(
            @Valid @RequestBody CandidatureRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        CandidatureResponse candidature = candidatureService.createCandidature(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(candidature);
    }

    /**
     * GET /api/candidatures/{id}
     * Retrieves an application by its ID.
     * Requires authentication.
     * 
     * @param id The application ID
     * @return Application response
     */
    @GetMapping("/{id}")
    public ResponseEntity<CandidatureResponse> getCandidatureById(@PathVariable Long id) {
        CandidatureResponse candidature = candidatureService.getCandidatureById(id);
        return ResponseEntity.ok(candidature);
    }

    /**
     * GET /api/candidatures/mes-candidatures
     * Retrieves all applications for the authenticated student.
     * Requires authentication - returns applications of the logged-in student.
     * 
     * @param authentication The authenticated user
     * @return List of application responses
     */
    @GetMapping("/mes-candidatures")
    public ResponseEntity<List<CandidatureResponse>> getMesCandidatures(Authentication authentication) {
        String email = authentication.getName();
        List<CandidatureResponse> candidatures = candidatureService.getCandidaturesByEtudiant(email);
        return ResponseEntity.ok(candidatures);
    }

    /**
     * GET /api/candidatures/offre/{offreId}
     * Retrieves all applications for a specific offer.
     * Requires authentication - only the offer owner can view applications.
     * 
     * @param offreId The offer ID
     * @param authentication The authenticated user
     * @return List of application responses
     */
    @GetMapping("/offre/{offreId}")
    public ResponseEntity<List<CandidatureResponse>> getCandidaturesByOffre(
            @PathVariable Long offreId,
            Authentication authentication) {
        String email = authentication.getName();
        List<CandidatureResponse> candidatures = candidatureService.getCandidaturesByOffre(offreId, email);
        return ResponseEntity.ok(candidatures);
    }

    /**
     * PUT /api/candidatures/{id}/accepter
     * Accepts an application.
     * Requires authentication - only the offer owner can accept applications.
     * Automatically generates a convention when an application is accepted (RG03).
     * 
     * @param id The application ID
     * @param authentication The authenticated user
     * @return Accepted application response
     */
    @PutMapping("/{id}/accepter")
    public ResponseEntity<CandidatureResponse> accepterCandidature(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        CandidatureResponse candidature = candidatureService.accepterCandidature(id, email);
        return ResponseEntity.ok(candidature);
    }

    /**
     * PUT /api/candidatures/{id}/refuser
     * Rejects an application.
     * Requires authentication - only the offer owner can reject applications.
     * 
     * @param id The application ID
     * @param body Optional request body with rejection comment
     * @param authentication The authenticated user
     * @return Rejected application response
     */
    @PutMapping("/{id}/refuser")
    public ResponseEntity<CandidatureResponse> refuserCandidature(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {
        String email = authentication.getName();
        String commentaire = body != null ? body.get("commentaire") : null;
        CandidatureResponse candidature = candidatureService.refuserCandidature(id, email, commentaire);
        return ResponseEntity.ok(candidature);
    }

    /**
     * DELETE /api/candidatures/{id}
     * Deletes an application.
     * Requires authentication - only the student owner can delete their applications.
     * Accepted applications cannot be deleted.
     * 
     * @param id The application ID
     * @param authentication The authenticated user
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidature(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        candidatureService.deleteCandidature(id, email);
        return ResponseEntity.noContent().build();
    }
}
