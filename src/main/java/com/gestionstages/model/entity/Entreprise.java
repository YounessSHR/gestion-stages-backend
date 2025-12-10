package com.gestionstages.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entreprises")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise extends Utilisateur {

    @NotBlank(message = "Nom de l'entreprise est obligatoire")
    @Size(min = 2, max = 200, message = "Le nom de l'entreprise doit contenir entre 2 et 200 caractères")
    @Column(nullable = false)
    private String nomEntreprise;

    @Size(max = 100, message = "Le secteur d'activité ne peut pas dépasser 100 caractères")
    private String secteurActivite;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String adresse;

    @Size(max = 255, message = "Le site web ne peut pas dépasser 255 caractères")
    private String siteWeb;

    @Size(max = 5000, message = "La description ne peut pas dépasser 5000 caractères")
    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OffreStage> offres = new ArrayList<>();

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
    private List<Convention> conventions = new ArrayList<>();
}