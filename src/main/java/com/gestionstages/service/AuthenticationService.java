package com.gestionstages.service;

import com.gestionstages.model.dto.request.LoginRequest;
import com.gestionstages.model.dto.request.RegisterRequest;
import com.gestionstages.model.dto.response.JwtResponse;
import com.gestionstages.model.dto.response.MessageResponse;

public interface AuthenticationService {
    
    JwtResponse login(LoginRequest loginRequest);
    
    MessageResponse register(RegisterRequest registerRequest);
}

