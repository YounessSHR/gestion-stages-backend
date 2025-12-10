package com.gestionstages.repository;

import com.gestionstages.model.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {

    List<Entreprise> findBySecteurActivite(String secteurActivite);

    List<Entreprise> findByNomEntrepriseContainingIgnoreCase(String nom);
}