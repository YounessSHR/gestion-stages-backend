package com.gestionstages.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "administrations")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Administration extends Utilisateur {
    // Hérite de tous les attributs et validations de Utilisateur
}