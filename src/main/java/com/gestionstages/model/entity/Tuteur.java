package com.gestionstages.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tuteur")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Tuteur extends Utilisateur {

    @Column(length = 100)
    private String departement;

    @Column(length = 100)
    private String specialite;

    @OneToMany(mappedBy = "tuteur", cascade = CascadeType.ALL)
    private List<SuiviStage> suivis = new ArrayList<>();
}