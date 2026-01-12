package com.gestionstages.service;

import com.gestionstages.model.dto.response.NotificationResponse;
import com.gestionstages.model.dto.response.PageResponse;

import java.util.List;

public interface NotificationService {
    void creerNotification(Long utilisateurId, String message, String type, String lienAction);
    List<NotificationResponse> getNotificationsByUser(String email);
    PageResponse<NotificationResponse> getNotificationsByUser(String email, int page, int size);
    Long getNombreNotificationsNonLues(String email);
    void marquerCommeLu(Long notificationId, String email);
    void marquerToutesCommeLues(String email);
}
