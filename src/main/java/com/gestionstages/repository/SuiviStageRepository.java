package com.gestionstages.repository;

import com.gestionstages.model.entity.SuiviStage;
import com.gestionstages.model.entity.Tuteur;
import com.gestionstages.model.enums.EtatAvancementEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuiviStageRepository extends JpaRepository<SuiviStage, Long> {

    List<SuiviStage> findByTuteur(Tuteur tuteur);

    List<SuiviStage> findByEtatAvancement(EtatAvancementEnum etat);

    @Query("SELECT s FROM SuiviStage s WHERE s.tuteur.email = :email")
    List<SuiviStage> findByTuteurEmail(@Param("email") String email);

    @Query("SELECT s FROM SuiviStage s WHERE s.convention.candidature.etudiant.email = :email")
    List<SuiviStage> findByConventionCandidatureEtudiantEmail(@Param("email") String email);

    @Query("SELECT COUNT(s) FROM SuiviStage s WHERE s.tuteur.email = :email AND s.etatAvancement != 'TERMINE'")
    Long countByTuteurEmailAndEtatAvancementNotTermine(@Param("email") String email);

    @Query("SELECT s FROM SuiviStage s WHERE s.convention.candidature.etudiant.email = :email AND s.etatAvancement != 'TERMINE'")
    List<SuiviStage> findActiveByEtudiantEmail(@Param("email") String email);

    Optional<SuiviStage> findByConventionId(Long conventionId);
}