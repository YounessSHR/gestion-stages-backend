package com.gestionstages.service.impl;

import com.gestionstages.exception.BadRequestException;
import com.gestionstages.exception.ResourceNotFoundException;
import com.gestionstages.exception.UnauthorizedException;
import com.gestionstages.model.dto.response.ConventionResponse;
import com.gestionstages.model.entity.Convention;
import com.gestionstages.model.enums.StatutConventionEnum;
import com.gestionstages.repository.ConventionRepository;
import com.gestionstages.service.ConventionService;
import com.gestionstages.service.PdfGeneratorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing conventions.
 * Handles signature management, PDF generation, and status transitions (RG04).
 */
@Service
public class ConventionServiceImpl implements ConventionService {

    @Autowired
    private ConventionRepository conventionRepository;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConventionResponse> getAllConventions() {
        List<Convention> conventions = conventionRepository.findAll();
        return conventions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConventionResponse getConventionById(Long id) {
        Convention convention = conventionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Convention non trouvée avec l'ID: " + id));
        return convertToResponse(convention);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConventionResponse> getConventionsByEtudiant(String emailEtudiant) {
        List<Convention> conventions = conventionRepository.findByCandidatureEtudiantEmail(emailEtudiant);
        return conventions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConventionResponse> getConventionsByEntreprise(String emailEntreprise) {
        List<Convention> conventions = conventionRepository.findByCandidatureOffreEntrepriseEmail(emailEntreprise);
        return conventions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConventionResponse signerEtudiant(Long conventionId, String emailEtudiant) {
        Convention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Convention non trouvée avec l'ID: " + conventionId));

        // Verify that the user is the student owner
        if (!convention.getCandidature().getEtudiant().getEmail().equals(emailEtudiant)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à signer cette convention");
        }

        // Check if already signed
        if (convention.getSignatureEtudiant()) {
            throw new BadRequestException("Vous avez déjà signé cette convention");
        }

        // Sign
        convention.setSignatureEtudiant(true);
        updateConventionStatus(convention);
        
        Convention savedConvention = conventionRepository.save(convention);
        return convertToResponse(savedConvention);
    }

    @Override
    @Transactional
    public ConventionResponse signerEntreprise(Long conventionId, String emailEntreprise) {
        Convention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Convention non trouvée avec l'ID: " + conventionId));

        // Verify that the user is the enterprise owner
        if (!convention.getCandidature().getOffre().getEntreprise().getEmail().equals(emailEntreprise)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à signer cette convention");
        }

        // Check if already signed
        if (convention.getSignatureEntreprise()) {
            throw new BadRequestException("L'entreprise a déjà signé cette convention");
        }

        // Sign
        convention.setSignatureEntreprise(true);
        updateConventionStatus(convention);
        
        Convention savedConvention = conventionRepository.save(convention);
        return convertToResponse(savedConvention);
    }

    @Override
    @Transactional
    public ConventionResponse signerAdmin(Long conventionId) {
        Convention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Convention non trouvée avec l'ID: " + conventionId));

        // Check if already signed
        if (convention.getSignatureAdministration()) {
            throw new BadRequestException("L'administration a déjà signé cette convention");
        }

        // Sign
        convention.setSignatureAdministration(true);
        updateConventionStatus(convention);
        
        Convention savedConvention = conventionRepository.save(convention);
        return convertToResponse(savedConvention);
    }

    @Override
    @Transactional
    public ConventionResponse genererPdf(Long conventionId) {
        Convention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Convention non trouvée avec l'ID: " + conventionId));

        // Only generate PDF if convention is signed (RG04)
        if (convention.getStatut() != StatutConventionEnum.SIGNEE) {
            throw new BadRequestException("Impossible de générer le PDF. La convention doit être signée par les trois parties.");
        }

        // Generate PDF
        try {
            String pdfFileName = pdfGeneratorService.generateConventionPdf(convention);
            convention.setFichierPdf(pdfFileName);
            Convention savedConvention = conventionRepository.save(convention);
            return convertToResponse(savedConvention);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ConventionResponse archiverConvention(Long conventionId) {
        Convention convention = conventionRepository.findById(conventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Convention non trouvée avec l'ID: " + conventionId));

        // Only signed conventions can be archived
        if (convention.getStatut() != StatutConventionEnum.SIGNEE) {
            throw new BadRequestException("Seules les conventions signées peuvent être archivées");
        }

        convention.setStatut(StatutConventionEnum.ARCHIVEE);
        Convention savedConvention = conventionRepository.save(convention);
        return convertToResponse(savedConvention);
    }

    /**
     * Updates convention status based on signatures.
     * Business Rule RG04: 
     * - BROUILLON → EN_ATTENTE_SIGNATURES (when first signature is added)
     * - EN_ATTENTE_SIGNATURES → SIGNEE (when all 3 signatures are collected)
     * - Automatically generates PDF when all 3 signatures are collected (SPRINT2_PLAN.md line 198)
     */
    private void updateConventionStatus(Convention convention) {
        boolean allSigned = convention.getSignatureEtudiant() && 
                           convention.getSignatureEntreprise() && 
                           convention.getSignatureAdministration();

        if (allSigned) {
            convention.setStatut(StatutConventionEnum.SIGNEE);
            
            // Automatically generate PDF when all 3 signatures are collected (SPRINT2_PLAN.md line 198)
            if (convention.getFichierPdf() == null || convention.getFichierPdf().isEmpty()) {
                try {
                    String pdfFileName = pdfGeneratorService.generateConventionPdf(convention);
                    convention.setFichierPdf(pdfFileName);
                } catch (Exception e) {
                    // Log error but don't fail the signature process
                    // PDF can be generated manually later if needed
                    System.err.println("Erreur lors de la génération automatique du PDF: " + e.getMessage());
                }
            }
        } else if (convention.getSignatureEtudiant() || 
                   convention.getSignatureEntreprise() || 
                   convention.getSignatureAdministration()) {
            // At least one signature, but not all
            convention.setStatut(StatutConventionEnum.EN_ATTENTE_SIGNATURES);
        }
        // Otherwise stays BROUILLON
    }

    /**
     * Converts a Convention entity to ConventionResponse DTO.
     * Maps entity fields to response DTO and includes related information.
     * 
     * @param convention The convention entity
     * @return Convention response DTO
     */
    private ConventionResponse convertToResponse(Convention convention) {
        ConventionResponse response = modelMapper.map(convention, ConventionResponse.class);
        response.setStatut(convention.getStatut().name());

        // Candidature information
        if (convention.getCandidature() != null) {
            response.setCandidatureId(convention.getCandidature().getId());

            // Student information
            if (convention.getCandidature().getEtudiant() != null) {
                response.setEtudiantId(convention.getCandidature().getEtudiant().getId());
                response.setEtudiantNom(convention.getCandidature().getEtudiant().getNom());
                response.setEtudiantPrenom(convention.getCandidature().getEtudiant().getPrenom());
                response.setEtudiantEmail(convention.getCandidature().getEtudiant().getEmail());
            }

            // Offre information
            if (convention.getCandidature().getOffre() != null) {
                response.setOffreId(convention.getCandidature().getOffre().getId());
                response.setOffreTitre(convention.getCandidature().getOffre().getTitre());

                // Enterprise information
                if (convention.getCandidature().getOffre().getEntreprise() != null) {
                    response.setEntrepriseId(convention.getCandidature().getOffre().getEntreprise().getId());
                    response.setEntrepriseNom(convention.getCandidature().getOffre().getEntreprise().getNomEntreprise());
                }
            }
        }

        // SuiviStage information
        if (convention.getSuiviStage() != null) {
            response.setSuiviStageId(convention.getSuiviStage().getId());
            response.setHasSuiviStage(true);
        } else {
            response.setHasSuiviStage(false);
        }

        return response;
    }
}
