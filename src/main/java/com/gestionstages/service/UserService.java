package com.gestionstages.service;

import com.gestionstages.model.dto.request.ChangePasswordRequest;
import com.gestionstages.model.dto.request.UpdateProfileRequest;
import com.gestionstages.model.dto.response.UserResponse;

public interface UserService {
    
    UserResponse getMyProfile(String email);
    
    UserResponse updateMyProfile(String email, UpdateProfileRequest request);
    
    void changePassword(String email, ChangePasswordRequest request);
    
    UserResponse getUserById(Long id);
}
