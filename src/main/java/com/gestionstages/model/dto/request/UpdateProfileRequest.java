package com.gestionstages.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Email(message = "Format email invalide")
    private String email;

    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;

    @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
    private String prenom;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    private String telephone;

    // Champs spécifiques Etudiant
    private String niveau;
    private String filiere;
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
