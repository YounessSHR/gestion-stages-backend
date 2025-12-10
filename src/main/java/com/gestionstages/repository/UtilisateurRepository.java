package com.gestionstages.repository;

import com.gestionstages.model.entity.Utilisateur;
import com.gestionstages.model.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<Utilisateur> findByRole(RoleEnum role);

    List<Utilisateur> findByActif(Boolean actif);
}