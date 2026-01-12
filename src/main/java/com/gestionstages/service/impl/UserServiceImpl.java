package com.gestionstages.service.impl;

import com.gestionstages.exception.BadRequestException;
import com.gestionstages.exception.ResourceNotFoundException;
import com.gestionstages.model.dto.request.ChangePasswordRequest;
import com.gestionstages.model.dto.request.UpdateProfileRequest;
import com.gestionstages.model.dto.response.UserResponse;
import com.gestionstages.model.entity.*;
import com.gestionstages.repository.*;
import com.gestionstages.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private TuteurRepository tuteurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        
        return convertToResponse(utilisateur);
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(String email, UpdateProfileRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Check if email is being changed and if it's already taken
        if (request.getEmail() != null && !request.getEmail().equals(email)) {
            if (utilisateurRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Cet email est déjà utilisé");
            }
            utilisateur.setEmail(request.getEmail());
        }

        // Update base fields
        if (request.getNom() != null) {
            utilisateur.setNom(request.getNom());
        }
        if (request.getPrenom() != null) {
            utilisateur.setPrenom(request.getPrenom());
        }
        if (request.getTelephone() != null) {
            utilisateur.setTelephone(request.getTelephone());
        }

        // Update role-specific fields
        updateRoleSpecificFields(utilisateur, request);

        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        return convertToResponse(savedUser);
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Verify old password
        if (!passwordEncoder.matches(request.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
            throw new BadRequestException("L'ancien mot de passe est incorrect");
        }

        // Check if new password is different
        if (passwordEncoder.matches(request.getNouveauMotDePasse(), utilisateur.getMotDePasse())) {
            throw new BadRequestException("Le nouveau mot de passe doit être différent de l'ancien");
        }

        // Update password
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        
        return convertToResponse(utilisateur);
    }

    private void updateRoleSpecificFields(Utilisateur utilisateur, UpdateProfileRequest request) {
        switch (utilisateur.getRole()) {
            case ETUDIANT:
                Etudiant etudiant = etudiantRepository.findById(utilisateur.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé"));
                if (request.getNiveau() != null) {
                    etudiant.setNiveau(request.getNiveau());
                }
                if (request.getFiliere() != null) {
                    etudiant.setFiliere(request.getFiliere());
                }
                if (request.getDateNaissance() != null) {
                    etudiant.setDateNaissance(request.getDateNaissance());
                }
                etudiantRepository.save(etudiant);
                break;

            case ENTREPRISE:
                Entreprise entreprise = entrepriseRepository.findById(utilisateur.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Entreprise non trouvée"));
                if (request.getNomEntreprise() != null) {
                    entreprise.setNomEntreprise(request.getNomEntreprise());
                }
                if (request.getSecteurActivite() != null) {
                    entreprise.setSecteurActivite(request.getSecteurActivite());
                }
                if (request.getAdresse() != null) {
                    entreprise.setAdresse(request.getAdresse());
                }
                if (request.getSiteWeb() != null) {
                    entreprise.setSiteWeb(request.getSiteWeb());
                }
                if (request.getDescription() != null) {
                    entreprise.setDescription(request.getDescription());
                }
                entrepriseRepository.save(entreprise);
                break;

            case TUTEUR:
                Tuteur tuteur = tuteurRepository.findById(utilisateur.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tuteur non trouvé"));
                if (request.getDepartement() != null) {
                    tuteur.setDepartement(request.getDepartement());
                }
                if (request.getSpecialite() != null) {
                    tuteur.setSpecialite(request.getSpecialite());
                }
                tuteurRepository.save(tuteur);
                break;

            case ADMINISTRATION:
                // Administration has no additional fields
                break;
        }
    }

    private UserResponse convertToResponse(Utilisateur utilisateur) {
        UserResponse response = new UserResponse();
        response.setId(utilisateur.getId());
        response.setEmail(utilisateur.getEmail());
        response.setNom(utilisateur.getNom());
        response.setPrenom(utilisateur.getPrenom());
        response.setTelephone(utilisateur.getTelephone());
        response.setRole(utilisateur.getRole().name());
        response.setDateCreation(utilisateur.getDateCreation());
        response.setActif(utilisateur.getActif());

        // Set role-specific fields
        switch (utilisateur.getRole()) {
            case ETUDIANT:
                Etudiant etudiant = etudiantRepository.findById(utilisateur.getId()).orElse(null);
                if (etudiant != null) {
                    response.setNiveau(etudiant.getNiveau());
                    response.setFiliere(etudiant.getFiliere());
                    response.setCv(etudiant.getCv());
                    response.setDateNaissance(etudiant.getDateNaissance());
                }
                break;

            case ENTREPRISE:
                Entreprise entreprise = entrepriseRepository.findById(utilisateur.getId()).orElse(null);
                if (entreprise != null) {
                    response.setNomEntreprise(entreprise.getNomEntreprise());
                    response.setSecteurActivite(entreprise.getSecteurActivite());
                    response.setAdresse(entreprise.getAdresse());
                    response.setSiteWeb(entreprise.getSiteWeb());
                    response.setDescription(entreprise.getDescription());
                }
                break;

            case TUTEUR:
                Tuteur tuteur = tuteurRepository.findById(utilisateur.getId()).orElse(null);
                if (tuteur != null) {
                    response.setDepartement(tuteur.getDepartement());
                    response.setSpecialite(tuteur.getSpecialite());
                }
                break;

            case ADMINISTRATION:
                // No additional fields
                break;
        }

        return response;
    }
}
