package com.gestionstages.repository;

import com.gestionstages.model.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    Optional<Etudiant> findByEmail(String email);

    List<Etudiant> findByNiveau(String niveau);

    List<Etudiant> findByFiliere(String filiere);

    List<Etudiant> findByNiveauAndFiliere(String niveau, String filiere);

    @Query("SELECT e FROM Etudiant e WHERE e.actif = true")
    List<Etudiant> findAllActifs();

    @Query("SELECT e FROM Etudiant e WHERE e.niveau = :niveau AND e.actif = true")
    List<Etudiant> findByNiveauAndActif(@Param("niveau") String niveau);

    @Query("SELECT COUNT(e) FROM Etudiant e WHERE e.filiere = :filiere")
    Long countByFiliere(@Param("filiere") String filiere);
}