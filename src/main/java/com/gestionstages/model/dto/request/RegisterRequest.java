package com.gestionstages.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;

    @NotBlank(message = "Nom est obligatoire")
    @Size(min = 2, max = 100)
    private String nom;

    @NotBlank(message = "Prénom est obligatoire")
    @Size(min = 2, max = 100)
    private String prenom;

    private String telephone;

    @NotBlank(message = "Role est obligatoire")
    private String role;

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