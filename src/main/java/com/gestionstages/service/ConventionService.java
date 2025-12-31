package com.gestionstages.service;

import com.gestionstages.model.dto.response.ConventionResponse;

import java.util.List;

/**
 * Service interface for managing conventions.
 * Handles signature management, PDF generation, and status transitions (RG04).
 */
public interface ConventionService {
    
    /**
     * Retrieves all conventions (admin only).
     * 
     * @return List of all convention responses
     */
    List<ConventionResponse> getAllConventions();
    
    /**
     * Retrieves a convention by its ID.
     * 
     * @param id The convention ID
     * @return Convention response
     */
    ConventionResponse getConventionById(Long id);
    
    /**
     * Retrieves all conventions for a specific student.
     * 
     * @param emailEtudiant The student email
     * @return List of convention responses
     */
    List<ConventionResponse> getConventionsByEtudiant(String emailEtudiant);
    
    /**
     * Retrieves all conventions for a specific enterprise.
     * 
     * @param emailEntreprise The enterprise email
     * @return List of convention responses
     */
    List<ConventionResponse> getConventionsByEntreprise(String emailEntreprise);
    
    /**
     * Student signs the convention.
     * Business Rule RG04: Only the student owner can sign.
     * Changes status from BROUILLON to EN_ATTENTE_SIGNATURES if first signature.
     * 
     * @param conventionId The convention ID
     * @param emailEtudiant The student email
     * @return Updated convention response
     */
    ConventionResponse signerEtudiant(Long conventionId, String emailEtudiant);
    
    /**
     * Enterprise signs the convention.
     * Business Rule RG04: Only the enterprise owner can sign.
     * Changes status from BROUILLON to EN_ATTENTE_SIGNATURES if first signature.
     * 
     * @param conventionId The convention ID
     * @param emailEntreprise The enterprise email
     * @return Updated convention response
     */
    ConventionResponse signerEntreprise(Long conventionId, String emailEntreprise);
    
    /**
     * Administration signs the convention.
     * Business Rule RG04: Only administration can sign.
     * Changes status from BROUILLON to EN_ATTENTE_SIGNATURES if first signature.
     * 
     * @param conventionId The convention ID
     * @return Updated convention response
     */
    ConventionResponse signerAdmin(Long conventionId);
    
    /**
     * Generates PDF for a convention.
     * Only generates PDF if convention is signed (status = SIGNEE).
     * 
     * @param conventionId The convention ID
     * @return Updated convention response with PDF path
     */
    ConventionResponse genererPdf(Long conventionId);
    
    /**
     * Archives a convention.
     * Only signed conventions can be archived.
     * 
     * @param conventionId The convention ID
     * @return Updated convention response
     */
    ConventionResponse archiverConvention(Long conventionId);
}
