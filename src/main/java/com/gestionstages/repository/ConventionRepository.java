package com.gestionstages.repository;

import com.gestionstages.model.entity.Convention;
import com.gestionstages.model.entity.Candidature;
import com.gestionstages.model.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConventionRepository extends JpaRepository<Convention, Long> {

    Optional<Convention> findByCandidature(Candidature candidature);

    List<Convention> findByEntreprise(Entreprise entreprise);

    List<Convention> findByStatut(String statut);

    @Query("SELECT c FROM Convention c WHERE c.candidature.etudiant.id = :etudiantId")
    List<Convention> findByEtudiantId(@Param("etudiantId") Long etudiantId);

    @Query("SELECT c FROM Convention c WHERE c.entreprise.id = :entrepriseId AND c.statut = :statut")
    List<Convention> findByEntrepriseIdAndStatut(@Param("entrepriseId") Long entrepriseId, @Param("statut") String statut);

    @Query("SELECT c FROM Convention c WHERE c.statut = 'EN_ATTENTE_SIGNATURES'")
    List<Convention> findConventionsEnAttenteSignatures();

    @Query("SELECT c FROM Convention c WHERE c.signatureEtudiant = false OR c.signatureEntreprise = false OR c.signatureAdministration = false")
    List<Convention> findConventionsNonSignees();

    @Query("SELECT c FROM Convention c WHERE c.signatureEtudiant = true AND c.signatureEntreprise = true AND c.signatureAdministration = true")
    List<Convention> findConventionsSignees();

    @Query("SELECT c FROM Convention c WHERE c.dateDebutStage <= :currentDate AND c.dateFinStage >= :currentDate AND c.statut = 'SIGNEE'")
    List<Convention> findConventionsEnCours(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(c) FROM Convention c WHERE c.entreprise.id = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT c FROM Convention c WHERE c.dateFinStage < :currentDate AND c.statut = 'SIGNEE'")
    List<Convention> findConventionsExpirees(@Param("currentDate") LocalDate currentDate);
}