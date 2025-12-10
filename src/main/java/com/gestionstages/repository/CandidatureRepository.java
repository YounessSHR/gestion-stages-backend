package com.gestionstages.repository;

import com.gestionstages.model.entity.Candidature;
import com.gestionstages.model.entity.Etudiant;
import com.gestionstages.model.entity.OffreStage;
import com.gestionstages.model.enums.StatutCandidatureEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    List<Candidature> findByEtudiant(Etudiant etudiant);

    List<Candidature> findByOffre(OffreStage offre);

    List<Candidature> findByStatut(StatutCandidatureEnum statut);

    Optional<Candidature> findByEtudiantAndOffre(Etudiant etudiant, OffreStage offre);

    Boolean existsByEtudiantAndOffre(Etudiant etudiant, OffreStage offre);
}