package com.gestionstages.repository;

import com.gestionstages.model.entity.SuiviStage;
import com.gestionstages.model.entity.Tuteur;
import com.gestionstages.model.entity.Convention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuiviStageRepository extends JpaRepository<SuiviStage, Long> {

    Optional<SuiviStage> findByConvention(Convention convention);

    List<SuiviStage> findByTuteur(Tuteur tuteur);

    List<SuiviStage> findByEtatAvancement(String etatAvancement);

    @Query("SELECT s FROM SuiviStage s WHERE s.tuteur.id = :tuteurId")
    List<SuiviStage> findByTuteurId(@Param("tuteurId") Long tuteurId);

    @Query("SELECT s FROM SuiviStage s WHERE s.tuteur.id = :tuteurId AND s.etatAvancement = :etat")
    List<SuiviStage> findByTuteurIdAndEtatAvancement(@Param("tuteurId") Long tuteurId, @Param("etat") String etat);

    @Query("SELECT s FROM SuiviStage s WHERE s.convention.candidature.etudiant.id = :etudiantId")
    Optional<SuiviStage> findByEtudiantId(@Param("etudiantId") Long etudiantId);

    @Query("SELECT s FROM SuiviStage s WHERE s.etatAvancement = 'EN_COURS'")
    List<SuiviStage> findStagesEnCours();

    @Query("SELECT COUNT(s) FROM SuiviStage s WHERE s.tuteur.id = :tuteurId")
    Long countByTuteurId(@Param("tuteurId") Long tuteurId);

    @Query("SELECT s FROM SuiviStage s WHERE s.derniereVisite IS NULL AND s.etatAvancement = 'EN_COURS'")
    List<SuiviStage> findStagesSansVisite();

    @Query("SELECT s FROM SuiviStage s WHERE s.tuteur.departement = :departement")
    List<SuiviStage> findByDepartement(@Param("departement") String departement);
}