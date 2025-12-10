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
@Table(name = "tuteurs")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Tuteur extends Utilisateur {

    @NotBlank(message = "Département est obligatoire")
    @Size(max = 100, message = "Le département ne peut pas dépasser 100 caractères")
    private String departement;

    @NotBlank(message = "Spécialité est obligatoire")
    @Size(max = 150, message = "La spécialité ne peut pas dépasser 150 caractères")
    private String specialite;

    @OneToMany(mappedBy = "tuteur", cascade = CascadeType.ALL)
    private List<SuiviStage> suivis = new ArrayList<>();
}