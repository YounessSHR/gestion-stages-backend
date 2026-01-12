package com.gestionstages.controller;

import com.gestionstages.exception.BadRequestException;
import com.gestionstages.exception.ResourceNotFoundException;
import com.gestionstages.exception.UnauthorizedException;
import com.gestionstages.model.entity.Etudiant;
import com.gestionstages.model.entity.Utilisateur;
import com.gestionstages.model.enums.RoleEnum;
import com.gestionstages.repository.EtudiantRepository;
import com.gestionstages.repository.UtilisateurRepository;
import com.gestionstages.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * REST Controller for managing CV uploads and downloads.
 * Only students can upload their own CV.
 */
@RestController
@RequestMapping("/api/cv")
@CrossOrigin(origins = "*")
public class CVController {

    private static final Logger logger = LoggerFactory.getLogger(CVController.class);

    // Allowed file types for CV
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "doc", "docx");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * POST /api/cv/upload
     * Uploads a CV file for the authenticated student.
     * 
     * @param file The CV file to upload
     * @param authentication The authenticated user
     * @return Success message with filename
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<?> uploadCV(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        String email = authentication.getName();
        
        try {
            // Verify user is a student
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
            
            if (utilisateur.getRole() != RoleEnum.ETUDIANT) {
                throw new UnauthorizedException("Seuls les étudiants peuvent uploader un CV");
            }

            // Validate file
            validateFile(file);

            // Get student entity
            Etudiant etudiant = etudiantRepository.findById(utilisateur.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

            // Delete old CV if exists
            if (etudiant.getCv() != null && !etudiant.getCv().isEmpty()) {
                fileStorageService.deleteFile(etudiant.getCv(), "cv");
            }

            // Store new CV
            String fileName = fileStorageService.storeFile(file, "cv");
            etudiant.setCv(fileName);
            etudiantRepository.save(etudiant);

            logger.info("CV uploaded successfully for student: {}", email);
            
            return ResponseEntity.ok().body(new java.util.HashMap<String, String>() {{
                put("message", "CV uploadé avec succès");
                put("filename", fileName);
            }});

        } catch (Exception e) {
            logger.error("Error uploading CV for student: {}", email, e);
            throw new BadRequestException("Erreur lors de l'upload du CV: " + e.getMessage());
        }
    }

    /**
     * GET /api/cv/download
     * Downloads the CV of the authenticated student.
     * 
     * @param authentication The authenticated user
     * @return CV file resource
     */
    @GetMapping("/download")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<Resource> downloadMyCV(Authentication authentication) {
        String email = authentication.getName();
        
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Etudiant etudiant = etudiantRepository.findById(utilisateur.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        if (etudiant.getCv() == null || etudiant.getCv().isEmpty()) {
            throw new ResourceNotFoundException("Aucun CV trouvé");
        }

        try {
            Resource resource = fileStorageService.loadFileAsResource(etudiant.getCv(), "cv");
            String contentType = "application/octet-stream";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + extractOriginalFilename(etudiant.getCv()) + "\"")
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error downloading CV for student: {}", email, e);
            throw new BadRequestException("Erreur lors du téléchargement du CV");
        }
    }

    /**
     * GET /api/cv/{etudiantId}
     * Downloads a student's CV (for enterprises viewing applications).
     * 
     * @param etudiantId The student ID
     * @param authentication The authenticated user
     * @return CV file resource
     */
    @GetMapping("/{etudiantId}")
    @PreAuthorize("hasAnyRole('ENTREPRISE', 'ADMINISTRATION')")
    public ResponseEntity<Resource> downloadStudentCV(
            @PathVariable Long etudiantId,
            Authentication authentication) {
        
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        if (etudiant.getCv() == null || etudiant.getCv().isEmpty()) {
            throw new ResourceNotFoundException("Cet étudiant n'a pas de CV");
        }

        try {
            Resource resource = fileStorageService.loadFileAsResource(etudiant.getCv(), "cv");
            String contentType = "application/octet-stream";
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"CV_" + etudiant.getNom() + "_" + etudiant.getPrenom() + ".pdf\"")
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error downloading CV for student ID: {}", etudiantId, e);
            throw new BadRequestException("Erreur lors du téléchargement du CV");
        }
    }

    /**
     * DELETE /api/cv
     * Deletes the CV of the authenticated student.
     * 
     * @param authentication The authenticated user
     * @return Success message
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<?> deleteCV(Authentication authentication) {
        String email = authentication.getName();
        
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        Etudiant etudiant = etudiantRepository.findById(utilisateur.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));

        if (etudiant.getCv() == null || etudiant.getCv().isEmpty()) {
            throw new ResourceNotFoundException("Aucun CV à supprimer");
        }

        try {
            fileStorageService.deleteFile(etudiant.getCv(), "cv");
            etudiant.setCv(null);
            etudiantRepository.save(etudiant);

            logger.info("CV deleted successfully for student: {}", email);
            
            return ResponseEntity.ok().body(new java.util.HashMap<String, String>() {{
                put("message", "CV supprimé avec succès");
            }});
        } catch (Exception e) {
            logger.error("Error deleting CV for student: {}", email, e);
            throw new BadRequestException("Erreur lors de la suppression du CV");
        }
    }

    /**
     * Validates the uploaded file.
     * 
     * @param file The file to validate
     * @throws BadRequestException if file is invalid
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Le fichier est vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Le fichier est trop volumineux. Taille maximale: 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BadRequestException("Nom de fichier invalide");
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Type de fichier non autorisé. Formats acceptés: PDF, DOC, DOCX");
        }
    }

    /**
     * Gets the file extension from a filename.
     * 
     * @param filename The filename
     * @return The file extension (without dot)
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * Extracts the original filename from a stored filename (removes UUID prefix).
     * 
     * @param storedFilename The stored filename
     * @return The original filename
     */
    private String extractOriginalFilename(String storedFilename) {
        int underscoreIndex = storedFilename.indexOf('_');
        if (underscoreIndex > 0 && underscoreIndex < storedFilename.length() - 1) {
            return storedFilename.substring(underscoreIndex + 1);
        }
        return storedFilename;
    }
}
