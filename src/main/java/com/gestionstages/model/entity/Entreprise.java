package com.gestionstages.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entreprise")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise extends Utilisateur {

    @Column(name = "nom_entreprise", nullable = false)
    private String nomEntreprise;

    @Column(name = "secteur_activite", length = 100)
    private String secteurActivite;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(name = "site_web", length = 255)
    private String siteWeb;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OffreStage> offres = new ArrayList<>();
}