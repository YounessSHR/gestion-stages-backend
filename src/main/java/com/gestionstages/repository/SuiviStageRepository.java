package com.gestionstages.repository;

import com.gestionstages.model.entity.SuiviStage;
import com.gestionstages.model.entity.Tuteur;
import com.gestionstages.model.enums.EtatAvancementEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuiviStageRepository extends JpaRepository<SuiviStage, Long> {

    List<SuiviStage> findByTuteur(Tuteur tuteur);

    List<SuiviStage> findByEtatAvancement(EtatAvancementEnum etat);
}