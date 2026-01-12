package com.gestionstages.repository;

import com.gestionstages.model.entity.Notification;
import com.gestionstages.model.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUtilisateurOrderByDateCreationDesc(Utilisateur utilisateur);
    Page<Notification> findByUtilisateurOrderByDateCreationDesc(Utilisateur utilisateur, Pageable pageable);
    Long countByUtilisateurAndLuFalse(Utilisateur utilisateur);
    List<Notification> findByUtilisateurAndLuFalseOrderByDateCreationDesc(Utilisateur utilisateur);
}
