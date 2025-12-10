package com.gestionstages.repository;

import com.gestionstages.model.entity.Entreprise;
import com.gestionstages.model.entity.OffreStage;
import com.gestionstages.model.enums.StatutOffreEnum;
import com.gestionstages.model.enums.TypeOffreEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OffreStageRepository extends JpaRepository<OffreStage, Long> {

    List<OffreStage> findByStatut(StatutOffreEnum statut);

    List<OffreStage> findByEntreprise(Entreprise entreprise);

    List<OffreStage> findByTypeOffre(TypeOffreEnum typeOffre);

    @Query("SELECT o FROM OffreStage o WHERE o.statut = :statut AND o.dateExpiration > :date")
    List<OffreStage> findActiveOffresByStatut(
            @Param("statut") StatutOffreEnum statut,
            @Param("date") LocalDate date
    );

    @Query("SELECT o FROM OffreStage o WHERE o.statut = 'VALIDEE' " +
            "AND (o.dateExpiration IS NULL OR o.dateExpiration > CURRENT_DATE)")
    List<OffreStage> findAllValidOffres();

    List<OffreStage> findByTitreContainingIgnoreCase(String titre);
}