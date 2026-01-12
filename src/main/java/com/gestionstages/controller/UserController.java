package com.gestionstages.controller;

import com.gestionstages.model.dto.request.ChangePasswordRequest;
import com.gestionstages.model.dto.request.UpdateProfileRequest;
import com.gestionstages.model.dto.response.MessageResponse;
import com.gestionstages.model.dto.response.UserResponse;
import com.gestionstages.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing user profiles.
 * Handles profile retrieval, updates, and password changes.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/users/me
     * Retrieves the profile of the authenticated user.
     * 
     * @param authentication The authenticated user
     * @return User profile response
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENTREPRISE', 'TUTEUR', 'ADMINISTRATION')")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        UserResponse profile = userService.getMyProfile(email);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/users/me
     * Updates the profile of the authenticated user.
     * 
     * @param request The profile update request
     * @param authentication The authenticated user
     * @return Updated user profile response
     */
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENTREPRISE', 'TUTEUR', 'ADMINISTRATION')")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        UserResponse updatedProfile = userService.updateMyProfile(email, request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * PUT /api/users/me/password
     * Changes the password of the authenticated user.
     * 
     * @param request The password change request
     * @param authentication The authenticated user
     * @return Success message
     */
    @PutMapping("/me/password")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENTREPRISE', 'TUTEUR', 'ADMINISTRATION')")
    public ResponseEntity<MessageResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok(new MessageResponse("Mot de passe modifié avec succès"));
    }

    /**
     * GET /api/users/{id}
     * Retrieves a user profile by ID.
     * Only accessible by administrators.
     * 
     * @param id The user ID
     * @return User profile response
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse profile = userService.getUserById(id);
        return ResponseEntity.ok(profile);
    }
}
