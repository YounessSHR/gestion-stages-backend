package com.gestionstages.repository;

import com.gestionstages.model.entity.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuteurRepository extends JpaRepository<Tuteur, Long> {

    Optional<Tuteur> findByEmail(String email);

    List<Tuteur> findByDepartement(String departement);

    List<Tuteur> findBySpecialite(String specialite);

    List<Tuteur> findByDepartementAndSpecialite(String departement, String specialite);

    @Query("SELECT t FROM Tuteur t WHERE t.actif = true")
    List<Tuteur> findAllActifs();

    @Query("SELECT t FROM Tuteur t WHERE t.departement = :departement AND t.actif = true")
    List<Tuteur> findByDepartementAndActif(@Param("departement") String departement);

    @Query("SELECT t FROM Tuteur t WHERE SIZE(t.suivis) < :maxSuivis AND t.actif = true")
    List<Tuteur> findTuteursDisponibles(@Param("maxSuivis") int maxSuivis);
}