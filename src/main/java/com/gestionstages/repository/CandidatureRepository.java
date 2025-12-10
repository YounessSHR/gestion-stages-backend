package com.gestionstages.repository;

import com.gestionstages.model.entity.Candidature;
import com.gestionstages.model.entity.Etudiant;
import com.gestionstages.model.entity.OffreStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    List<Candidature> findByEtudiant(Etudiant etudiant);

    List<Candidature> findByOffre(OffreStage offre);

    List<Candidature> findByStatut(String statut);

    Optional<Candidature> findByEtudiantAndOffre(Etudiant etudiant, OffreStage offre);

    @Query("SELECT c FROM Candidature c WHERE c.etudiant.id = :etudiantId AND c.statut = :statut")
    List<Candidature> findByEtudiantIdAndStatut(@Param("etudiantId") Long etudiantId, @Param("statut") String statut);

    @Query("SELECT c FROM Candidature c WHERE c.offre.id = :offreId AND c.statut = :statut")
    List<Candidature> findByOffreIdAndStatut(@Param("offreId") Long offreId, @Param("statut") String statut);

    @Query("SELECT c FROM Candidature c WHERE c.offre.entreprise.id = :entrepriseId")
    List<Candidature> findByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT c FROM Candidature c WHERE c.offre.entreprise.id = :entrepriseId AND c.statut = :statut")
    List<Candidature> findByEntrepriseIdAndStatut(@Param("entrepriseId") Long entrepriseId, @Param("statut") String statut);

    @Query("SELECT COUNT(c) FROM Candidature c WHERE c.etudiant.id = :etudiantId")
    Long countByEtudiantId(@Param("etudiantId") Long etudiantId);

    @Query("SELECT COUNT(c) FROM Candidature c WHERE c.offre.id = :offreId")
    Long countByOffreId(@Param("offreId") Long offreId);

    boolean existsByEtudiantAndOffre(Etudiant etudiant, OffreStage offre);

    @Query("SELECT c FROM Candidature c WHERE c.dateCandidature BETWEEN :startDate AND :endDate")
    List<Candidature> findByDateCandidatureBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}