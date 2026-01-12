package com.gestionstages.service.impl;

import com.gestionstages.model.dto.response.NotificationResponse;
import com.gestionstages.model.dto.response.PageResponse;
import com.gestionstages.model.entity.Notification;
import com.gestionstages.model.entity.Utilisateur;
import com.gestionstages.repository.NotificationRepository;
import com.gestionstages.repository.UtilisateurRepository;
import com.gestionstages.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Async("taskExecutor")
    @Transactional
    public void creerNotification(Long utilisateurId, String message, String type, String lienAction) {
        try {
            Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                    .orElse(null);
            
            if (utilisateur == null) {
                return; // Silently fail if user not found
            }
            
            Notification notification = new Notification();
            notification.setUtilisateur(utilisateur);
            notification.setMessage(message);
            notification.setType(type);
            notification.setLienAction(lienAction);
            notification.setLu(false);
            
            notificationRepository.save(notification);
        } catch (Exception e) {
            // Log but don't throw - notifications are non-critical
            System.err.println("Erreur lors de la création de la notification: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUser(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return notificationRepository.findByUtilisateurOrderByDateCreationDesc(utilisateur)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getNotificationsByUser(String email, int page, int size) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> pageResult = notificationRepository.findByUtilisateurOrderByDateCreationDesc(utilisateur, pageable);
        
        List<NotificationResponse> content = pageResult.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isFirst(),
                pageResult.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Long getNombreNotificationsNonLues(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        return notificationRepository.countByUtilisateurAndLuFalse(utilisateur);
    }

    @Override
    @Transactional
    public void marquerCommeLu(Long notificationId, String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        
        if (!notification.getUtilisateur().getId().equals(utilisateur.getId())) {
            throw new RuntimeException("Non autorisé");
        }
        
        notification.setLu(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void marquerToutesCommeLues(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        List<Notification> notifications = notificationRepository.findByUtilisateurAndLuFalseOrderByDateCreationDesc(utilisateur);
        notifications.forEach(n -> n.setLu(true));
        notificationRepository.saveAll(notifications);
    }

    private NotificationResponse convertToResponse(Notification notification) {
        return modelMapper.map(notification, NotificationResponse.class);
    }
}
