package com.gestionstages.service;

import com.gestionstages.model.dto.response.DashboardStatsResponse;

/**
 * Service interface for administration dashboard statistics.
 */
public interface DashboardService {
    
    /**
     * Retrieves comprehensive statistics for the admin dashboard.
     * 
     * @return Dashboard statistics response
     */
    DashboardStatsResponse getDashboardStats();
}

