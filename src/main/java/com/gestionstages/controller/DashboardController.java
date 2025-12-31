package com.gestionstages.controller;

import com.gestionstages.model.dto.response.DashboardStatsResponse;
import com.gestionstages.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for administration dashboard.
 * Provides statistics and analytics for administrators.
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * GET /api/admin/dashboard/stats
     * Retrieves comprehensive statistics for the admin dashboard.
     * Requires authentication - admin only.
     * 
     * @return Dashboard statistics response
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}

