package com.gestionstages.repository;

import com.gestionstages.model.entity.OffreStage;
import com.gestionstages.model.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OffreStageRepository extends JpaRepository<OffreStage, Long> {

    List<OffreStage> findByEntreprise(Entreprise entreprise);

    List<OffreStage> findByStatut(String statut);

    List<OffreStage> findByTypeOffre(String typeOffre);

    List<OffreStage> findByStatutAndTypeOffre(String statut, String typeOffre);

    @Query("SELECT o FROM OffreStage o WHERE o.statut = 'VALIDEE' AND o.dateExpiration > :currentDate")
    List<OffreStage> findOffresValides(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT o FROM OffreStage o WHERE o.entreprise.id = :entrepriseId AND o.statut = :statut")
    List<OffreStage> findByEntrepriseIdAndStatut(@Param("entrepriseId") Long entrepriseId, @Param("statut") String statut);

    @Query("SELECT o FROM OffreStage o WHERE LOWER(o.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<OffreStage> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT o FROM OffreStage o WHERE o.statut = 'VALIDEE' AND o.dateExpiration > :currentDate AND o.typeOffre = :type")
    List<OffreStage> findOffresValidesParType(@Param("currentDate") LocalDate currentDate, @Param("type") String type);

    @Query("SELECT COUNT(o) FROM OffreStage o WHERE o.entreprise.id = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT o FROM OffreStage o WHERE o.dateExpiration < :currentDate AND o.statut = 'VALIDEE'")
    List<OffreStage> findOffresExpirees(@Param("currentDate") LocalDate currentDate);
}