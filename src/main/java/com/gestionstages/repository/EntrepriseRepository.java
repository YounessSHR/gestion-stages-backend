package com.gestionstages.repository;

import com.gestionstages.model.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {

    Optional<Entreprise> findByEmail(String email);

    Optional<Entreprise> findByNomEntreprise(String nomEntreprise);

    List<Entreprise> findBySecteurActivite(String secteurActivite);

    @Query("SELECT e FROM Entreprise e WHERE e.actif = true")
    List<Entreprise> findAllActives();

    @Query("SELECT e FROM Entreprise e WHERE e.secteurActivite = :secteur AND e.actif = true")
    List<Entreprise> findBySecteurAndActif(@Param("secteur") String secteur);

    @Query("SELECT e FROM Entreprise e WHERE LOWER(e.nomEntreprise) LIKE LOWER(CONCAT('%', :nom, '%'))")
    List<Entreprise> searchByNom(@Param("nom") String nom);

    boolean existsByNomEntreprise(String nomEntreprise);
}