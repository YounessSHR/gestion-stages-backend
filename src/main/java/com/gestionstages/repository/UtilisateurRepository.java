package com.gestionstages.repository;

import com.gestionstages.model.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Utilisateur> findByRole(String role);

    List<Utilisateur> findByActif(Boolean actif);

    Optional<Utilisateur> findByEmailAndActif(String email, Boolean actif);
}