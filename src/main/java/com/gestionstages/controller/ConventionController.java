package com.gestionstages.controller;

import com.gestionstages.model.dto.response.ConventionResponse;
import com.gestionstages.service.ConventionService;
import com.gestionstages.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing conventions.
 * Handles signature operations, PDF generation, and convention retrieval.
 */
@RestController
@RequestMapping("/api/conventions")
@CrossOrigin(origins = "*")
public class ConventionController {

    @Autowired
    private ConventionService conventionService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    /**
     * GET /api/conventions
     * Retrieves all conventions.
     * Requires authentication - admin only (can be filtered by role in service if needed).
     * 
     * @param authentication The authenticated user
     * @return List of convention responses
     */
    @GetMapping
    public ResponseEntity<List<ConventionResponse>> getAllConventions(Authentication authentication) {
        List<ConventionResponse> conventions = conventionService.getAllConventions();
        return ResponseEntity.ok(conventions);
    }

    /**
     * GET /api/conventions/{id}
     * Retrieves a convention by its ID.
     * Requires authentication.
     * 
     * @param id The convention ID
     * @return Convention response
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConventionResponse> getConventionById(@PathVariable Long id) {
        ConventionResponse convention = conventionService.getConventionById(id);
        return ResponseEntity.ok(convention);
    }

    /**
     * GET /api/conventions/mes-conventions
     * Retrieves all conventions for the authenticated user (student or enterprise).
     * Requires authentication.
     * 
     * @param authentication The authenticated user
     * @return List of convention responses
     */
    @GetMapping("/mes-conventions")
    public ResponseEntity<List<ConventionResponse>> getMesConventions(Authentication authentication) {
        String email = authentication.getName();
        // This will be determined based on user role - for now, check both
        // In a real scenario, you'd check the role and call appropriate method
        List<ConventionResponse> conventions = conventionService.getConventionsByEtudiant(email);
        if (conventions.isEmpty()) {
            conventions = conventionService.getConventionsByEntreprise(email);
        }
        return ResponseEntity.ok(conventions);
    }

    /**
     * GET /api/conventions/etudiant
     * Retrieves all conventions for a specific student.
     * Requires authentication.
     * 
     * @param authentication The authenticated user
     * @return List of convention responses
     */
    @GetMapping("/etudiant")
    public ResponseEntity<List<ConventionResponse>> getConventionsByEtudiant(Authentication authentication) {
        String email = authentication.getName();
        List<ConventionResponse> conventions = conventionService.getConventionsByEtudiant(email);
        return ResponseEntity.ok(conventions);
    }

    /**
     * GET /api/conventions/entreprise
     * Retrieves all conventions for a specific enterprise.
     * Requires authentication.
     * 
     * @param authentication The authenticated user
     * @return List of convention responses
     */
    @GetMapping("/entreprise")
    public ResponseEntity<List<ConventionResponse>> getConventionsByEntreprise(Authentication authentication) {
        String email = authentication.getName();
        List<ConventionResponse> conventions = conventionService.getConventionsByEntreprise(email);
        return ResponseEntity.ok(conventions);
    }

    /**
     * PUT /api/conventions/{id}/signer-etudiant
     * Student signs the convention.
     * Requires authentication - student owner only.
     * 
     * @param id The convention ID
     * @param authentication The authenticated user
     * @return Updated convention response
     */
    @PutMapping("/{id}/signer-etudiant")
    public ResponseEntity<ConventionResponse> signerEtudiant(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        ConventionResponse convention = conventionService.signerEtudiant(id, email);
        return ResponseEntity.ok(convention);
    }

    /**
     * PUT /api/conventions/{id}/signer-entreprise
     * Enterprise signs the convention.
     * Requires authentication - enterprise owner only.
     * 
     * @param id The convention ID
     * @param authentication The authenticated user
     * @return Updated convention response
     */
    @PutMapping("/{id}/signer-entreprise")
    public ResponseEntity<ConventionResponse> signerEntreprise(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        ConventionResponse convention = conventionService.signerEntreprise(id, email);
        return ResponseEntity.ok(convention);
    }

    /**
     * PUT /api/conventions/{id}/signer-admin
     * Administration signs the convention.
     * Requires authentication - admin only.
     * 
     * @param id The convention ID
     * @return Updated convention response
     */
    @PutMapping("/{id}/signer-admin")
    public ResponseEntity<ConventionResponse> signerAdmin(@PathVariable Long id) {
        ConventionResponse convention = conventionService.signerAdmin(id);
        return ResponseEntity.ok(convention);
    }

    /**
     * POST /api/conventions/{id}/generer-pdf
     * Generates PDF for a convention.
     * Requires authentication - convention must be signed.
     * 
     * @param id The convention ID
     * @return Updated convention response with PDF path
     */
    @PostMapping("/{id}/generer-pdf")
    public ResponseEntity<ConventionResponse> genererPdf(@PathVariable Long id) {
        ConventionResponse convention = conventionService.genererPdf(id);
        return ResponseEntity.ok(convention);
    }

    /**
     * GET /api/conventions/{id}/pdf
     * Downloads the PDF file of a convention.
     * Requires authentication.
     * 
     * @param id The convention ID
     * @return PDF file resource
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
        ConventionResponse convention = conventionService.getConventionById(id);
        
        if (convention.getFichierPdf() == null || convention.getFichierPdf().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = pdfGeneratorService.getPdfResource(convention.getFichierPdf());
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"convention_" + id + ".pdf\"")
                .body(resource);
    }

    /**
     * PUT /api/conventions/{id}/archiver
     * Archives a convention.
     * Requires authentication - admin only.
     * 
     * @param id The convention ID
     * @return Updated convention response
     */
    @PutMapping("/{id}/archiver")
    public ResponseEntity<ConventionResponse> archiverConvention(@PathVariable Long id) {
        ConventionResponse convention = conventionService.archiverConvention(id);
        return ResponseEntity.ok(convention);
    }
}
