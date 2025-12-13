package com.gestionstages.repository;

import com.gestionstages.model.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    List<Etudiant> findByFiliere(String filiere);

    List<Etudiant> findByNiveau(String niveau);
    
    Optional<Etudiant> findByEmail(String email);
}