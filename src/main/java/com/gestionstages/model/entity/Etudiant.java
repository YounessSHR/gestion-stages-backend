package com.gestionstages.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etudiant")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant extends Utilisateur {

    @Column(length = 50)
    private String niveau;

    @Column(length = 100)
    private String filiere;

    @Column(length = 255)
    private String cv;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures = new ArrayList<>();
}