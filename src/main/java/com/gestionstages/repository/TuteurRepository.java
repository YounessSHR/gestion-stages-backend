package com.gestionstages.repository;

import com.gestionstages.model.entity.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuteurRepository extends JpaRepository<Tuteur, Long> {

    List<Tuteur> findByDepartement(String departement);

    List<Tuteur> findBySpecialite(String specialite);

    Optional<Tuteur> findByEmail(String email);
}