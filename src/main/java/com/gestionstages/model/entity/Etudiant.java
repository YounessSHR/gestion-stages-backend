package com.gestionstages.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etudiants")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant extends Utilisateur {

    @NotBlank(message = "Niveau est obligatoire")
    @Size(max = 50, message = "Le niveau ne peut pas dépasser 50 caractères")
    private String niveau;

    @NotBlank(message = "Filière est obligatoire")
    @Size(max = 100, message = "La filière ne peut pas dépasser 100 caractères")
    private String filiere;

    @Size(max = 255, message = "Le chemin CV ne peut pas dépasser 255 caractères")
    private String cv;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures = new ArrayList<>();
}