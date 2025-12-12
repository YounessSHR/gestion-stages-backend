package com.gestionstages.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "administration")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Administration extends Utilisateur {
    // Pas de champs supplémentaires pour l'instant
}