package com.gestionstages.repository;

import com.gestionstages.model.entity.Candidature;
import com.gestionstages.model.entity.Convention;
import com.gestionstages.model.enums.StatutConventionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConventionRepository extends JpaRepository<Convention, Long> {

    Optional<Convention> findByCandidature(Candidature candidature);

    List<Convention> findByStatut(StatutConventionEnum statut);
}