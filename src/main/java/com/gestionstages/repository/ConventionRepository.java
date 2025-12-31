package com.gestionstages.repository;

import com.gestionstages.model.entity.Candidature;
import com.gestionstages.model.entity.Convention;
import com.gestionstages.model.enums.StatutConventionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConventionRepository extends JpaRepository<Convention, Long> {

    Optional<Convention> findByCandidature(Candidature candidature);

    List<Convention> findByStatut(StatutConventionEnum statut);

    @Query("SELECT c FROM Convention c WHERE c.candidature.etudiant.email = :email")
    List<Convention> findByCandidatureEtudiantEmail(@Param("email") String email);

    @Query("SELECT c FROM Convention c WHERE c.candidature.offre.entreprise.email = :email")
    List<Convention> findByCandidatureOffreEntrepriseEmail(@Param("email") String email);

    Optional<Convention> findByCandidatureId(Long candidatureId);
}