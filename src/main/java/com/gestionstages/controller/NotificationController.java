package com.gestionstages.controller;

import com.gestionstages.model.dto.response.NotificationResponse;
import com.gestionstages.model.dto.response.PageResponse;
import com.gestionstages.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication authentication) {
        String email = authentication.getName();
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(email);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<NotificationResponse>> getNotificationsPage(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = authentication.getName();
        PageResponse<NotificationResponse> result = notificationService.getNotificationsByUser(email, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getNombreNonLues(Authentication authentication) {
        String email = authentication.getName();
        Long count = notificationService.getNombreNotificationsNonLues(email);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/lu")
    public ResponseEntity<Void> marquerCommeLu(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        notificationService.marquerCommeLu(id, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/toutes-lues")
    public ResponseEntity<Void> marquerToutesCommeLues(Authentication authentication) {
        String email = authentication.getName();
        notificationService.marquerToutesCommeLues(email);
        return ResponseEntity.noContent().build();
    }
}
