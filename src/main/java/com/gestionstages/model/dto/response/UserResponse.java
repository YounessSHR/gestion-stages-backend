package com.gestionstages.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private String role;
    private LocalDateTime dateCreation;
    private Boolean actif;

    // Champs spécifiques Etudiant
    private String niveau;
    private String filiere;
    private String cv;
    private LocalDate dateNaissance;

    // Champs spécifiques Entreprise
    private String nomEntreprise;
    private String secteurActivite;
    private String adresse;
    private String siteWeb;
    private String description;

    // Champs spécifiques Tuteur
    private String departement;
    private String specialite;
}