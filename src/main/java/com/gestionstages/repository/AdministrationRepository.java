package com.gestionstages.repository;

import com.gestionstages.model.entity.Administration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdministrationRepository extends JpaRepository<Administration, Long> {

    Optional<Administration> findByEmail(String email);

    @Query("SELECT a FROM Administration a WHERE a.actif = true")
    List<Administration> findAllActifs();

    boolean existsByEmail(String email);
}