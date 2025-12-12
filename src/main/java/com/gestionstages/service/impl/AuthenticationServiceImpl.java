package com.gestionstages.service.impl;

import com.gestionstages.exception.BadRequestException;
import com.gestionstages.model.dto.request.LoginRequest;
import com.gestionstages.model.dto.request.RegisterRequest;
import com.gestionstages.model.dto.response.JwtResponse;
import com.gestionstages.model.dto.response.MessageResponse;
import com.gestionstages.model.entity.*;
import com.gestionstages.model.enums.RoleEnum;
import com.gestionstages.repository.*;
import com.gestionstages.security.jwt.JwtTokenProvider;
import com.gestionstages.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private TuteurRepository tuteurRepository;

    @Autowired
    private AdministrationRepository administrationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getMotDePasse()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);

        // Get user details
        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("Utilisateur non trouvé"));

        return new JwtResponse(
                jwt,
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getRole().name()
        );
    }

    @Override
    @Transactional
    public MessageResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (utilisateurRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("L'email est déjà utilisé");
        }

        // Validate role
        RoleEnum role;
        try {
            role = RoleEnum.valueOf(registerRequest.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Role invalide: " + registerRequest.getRole());
        }

        // Create user based on role
        Utilisateur utilisateur = null;

        switch (role) {
            case ETUDIANT:
                utilisateur = createEtudiant(registerRequest);
                etudiantRepository.save((Etudiant) utilisateur);
                break;
            case ENTREPRISE:
                utilisateur = createEntreprise(registerRequest);
                entrepriseRepository.save((Entreprise) utilisateur);
                break;
            case TUTEUR:
                utilisateur = createTuteur(registerRequest);
                tuteurRepository.save((Tuteur) utilisateur);
                break;
            case ADMINISTRATION:
                utilisateur = createAdministration(registerRequest);
                administrationRepository.save((Administration) utilisateur);
                break;
            default:
                throw new BadRequestException("Role non supporté: " + role);
        }

        return new MessageResponse("Inscription réussie pour " + utilisateur.getEmail());
    }

    private Etudiant createEtudiant(RegisterRequest request) {
        Etudiant etudiant = new Etudiant();
        setBaseUserFields(etudiant, request);
        etudiant.setNiveau(request.getNiveau());
        etudiant.setFiliere(request.getFiliere());
        etudiant.setDateNaissance(request.getDateNaissance());
        return etudiant;
    }

    private Entreprise createEntreprise(RegisterRequest request) {
        if (request.getNomEntreprise() == null || request.getNomEntreprise().trim().isEmpty()) {
            throw new BadRequestException("Le nom de l'entreprise est obligatoire");
        }

        Entreprise entreprise = new Entreprise();
        setBaseUserFields(entreprise, request);
        entreprise.setNomEntreprise(request.getNomEntreprise());
        entreprise.setSecteurActivite(request.getSecteurActivite());
        entreprise.setAdresse(request.getAdresse());
        entreprise.setSiteWeb(request.getSiteWeb());
        entreprise.setDescription(request.getDescription());
        return entreprise;
    }

    private Tuteur createTuteur(RegisterRequest request) {
        Tuteur tuteur = new Tuteur();
        setBaseUserFields(tuteur, request);
        tuteur.setDepartement(request.getDepartement());
        tuteur.setSpecialite(request.getSpecialite());
        return tuteur;
    }

    private Administration createAdministration(RegisterRequest request) {
        Administration administration = new Administration();
        setBaseUserFields(administration, request);
        return administration;
    }

    private void setBaseUserFields(Utilisateur utilisateur, RegisterRequest request) {
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setTelephone(request.getTelephone());
        
        RoleEnum role;
        try {
            role = RoleEnum.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Role invalide: " + request.getRole());
        }
        utilisateur.setRole(role);
        utilisateur.setActif(true);
    }
}

