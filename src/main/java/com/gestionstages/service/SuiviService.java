package com.gestionstages.service;

import com.gestionstages.model.dto.request.AssignTuteurRequest;
import com.gestionstages.model.dto.request.UpdateSuiviRequest;
import com.gestionstages.model.dto.response.SuiviStageResponse;

import java.util.List;

/**
 * Service interface for managing stage follow-up (suivi stages).
 * Handles tutor assignments, progress tracking, and business rules (RG05, RG07).
 */
public interface SuiviService {
    
    /**
     * Assigns a tutor to a signed convention.
     * Business Rule RG05: A tutor can follow max 10 students.
     * Business Rule RG07: A student can only have one active internship at a time.
     * 
     * @param request The assignment request (conventionId, tuteurId)
     * @return Created suivi stage response
     */
    SuiviStageResponse assignerTuteur(AssignTuteurRequest request);
    
    /**
     * Retrieves all suivi stages (admin only).
     * 
     * @return List of all suivi stage responses
     */
    List<SuiviStageResponse> getAllSuivis();
    
    /**
     * Retrieves a suivi stage by its ID.
     * 
     * @param id The suivi stage ID
     * @return Suivi stage response
     */
    SuiviStageResponse getSuiviById(Long id);
    
    /**
     * Retrieves all students followed by a tutor.
     * 
     * @param emailTuteur The tutor email
     * @return List of suivi stage responses
     */
    List<SuiviStageResponse> getMesEtudiants(String emailTuteur);
    
    /**
     * Retrieves the active internship for a student.
     * 
     * @param emailEtudiant The student email
     * @return Suivi stage response (or null if none)
     */
    SuiviStageResponse getMonStage(String emailEtudiant);
    
    /**
     * Updates the progress of a stage (tutor only).
     * 
     * @param id The suivi stage ID
     * @param request The update request (etatAvancement, commentaires, derniereVisite)
     * @param emailTuteur The tutor email
     * @return Updated suivi stage response
     */
    SuiviStageResponse updateSuivi(Long id, UpdateSuiviRequest request, String emailTuteur);
}
