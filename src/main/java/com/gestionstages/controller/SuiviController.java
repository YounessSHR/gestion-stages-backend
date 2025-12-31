package com.gestionstages.controller;

import com.gestionstages.model.dto.request.AssignTuteurRequest;
import com.gestionstages.model.dto.request.UpdateSuiviRequest;
import com.gestionstages.model.dto.response.SuiviStageResponse;
import com.gestionstages.service.SuiviService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing stage follow-up (suivi stages).
 * Handles tutor assignments, progress tracking, and student-tutor relationships.
 */
@RestController
@RequestMapping("/api/suivis")
@CrossOrigin(origins = "*")
public class SuiviController {

    @Autowired
    private SuiviService suiviService;

    /**
     * POST /api/suivis/assigner-tuteur
     * Assigns a tutor to a signed convention.
     * Requires authentication - admin only.
     * 
     * @param request The assignment request
     * @return Created suivi stage response
     */
    @PostMapping("/assigner-tuteur")
    public ResponseEntity<SuiviStageResponse> assignerTuteur(
            @Valid @RequestBody AssignTuteurRequest request) {
        SuiviStageResponse suiviStage = suiviService.assignerTuteur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(suiviStage);
    }

    /**
     * GET /api/suivis
     * Retrieves all suivi stages.
     * Requires authentication - admin only.
     * 
     * @return List of all suivi stage responses
     */
    @GetMapping
    public ResponseEntity<List<SuiviStageResponse>> getAllSuivis() {
        List<SuiviStageResponse> suivis = suiviService.getAllSuivis();
        return ResponseEntity.ok(suivis);
    }

    /**
     * GET /api/suivis/{id}
     * Retrieves a suivi stage by its ID.
     * Requires authentication.
     * 
     * @param id The suivi stage ID
     * @return Suivi stage response
     */
    @GetMapping("/{id}")
    public ResponseEntity<SuiviStageResponse> getSuiviById(@PathVariable Long id) {
        SuiviStageResponse suiviStage = suiviService.getSuiviById(id);
        return ResponseEntity.ok(suiviStage);
    }

    /**
     * GET /api/suivis/mes-etudiants
     * Retrieves all students followed by the authenticated tutor.
     * Requires authentication - tutor only.
     * 
     * @param authentication The authenticated user
     * @return List of suivi stage responses
     */
    @GetMapping("/mes-etudiants")
    public ResponseEntity<List<SuiviStageResponse>> getMesEtudiants(Authentication authentication) {
        String email = authentication.getName();
        List<SuiviStageResponse> suivis = suiviService.getMesEtudiants(email);
        return ResponseEntity.ok(suivis);
    }

    /**
     * GET /api/suivis/mon-stage
     * Retrieves the active internship for the authenticated student.
     * Requires authentication - student only.
     * 
     * @param authentication The authenticated user
     * @return Suivi stage response (or 404 if none)
     */
    @GetMapping("/mon-stage")
    public ResponseEntity<SuiviStageResponse> getMonStage(Authentication authentication) {
        String email = authentication.getName();
        SuiviStageResponse suiviStage = suiviService.getMonStage(email);
        if (suiviStage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(suiviStage);
    }

    /**
     * PUT /api/suivis/{id}/avancement
     * Updates the progress of a stage (tutor only).
     * Requires authentication - tutor owner only.
     * 
     * @param id The suivi stage ID
     * @param request The update request
     * @param authentication The authenticated user
     * @return Updated suivi stage response
     */
    @PutMapping("/{id}/avancement")
    public ResponseEntity<SuiviStageResponse> updateSuivi(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSuiviRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        SuiviStageResponse suiviStage = suiviService.updateSuivi(id, request, email);
        return ResponseEntity.ok(suiviStage);
    }
}
