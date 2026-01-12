package com.gestionstages.service;

import com.gestionstages.model.dto.request.OffreFilterRequest;
import com.gestionstages.model.dto.request.OffreRequest;
import com.gestionstages.model.dto.response.OffreResponse;
import com.gestionstages.model.dto.response.PageResponse;

import java.util.List;

public interface OffreService {
    
    /**
     * Récupère toutes les offres publiques (validées et non expirées) avec pagination et filtres
     * Accessible à tous (étudiants)
     */
    PageResponse<OffreResponse> getOffresPubliques(OffreFilterRequest filter);
    
    /**
     * Récupère toutes les offres publiques (validées et non expirées)
     * Accessible à tous (étudiants)
     * @deprecated Use getOffresPubliques(OffreFilterRequest) instead
     */
    @Deprecated
    List<OffreResponse> getAllOffresPubliques();
    
    /**
     * Récupère une offre par son ID
     */
    OffreResponse getOffreById(Long id);
    
    /**
     * Crée une nouvelle offre (entreprise uniquement)
     * L'offre est créée avec le statut EN_ATTENTE
     */
    OffreResponse createOffre(OffreRequest request, String emailEntreprise);
    
    /**
     * Met à jour une offre (entreprise propriétaire uniquement)
     */
    OffreResponse updateOffre(Long id, OffreRequest request, String emailEntreprise);
    
    /**
     * Supprime une offre (entreprise propriétaire uniquement)
     */
    void deleteOffre(Long id, String emailEntreprise);
    
    /**
     * Valide une offre (administration uniquement)
     * Change le statut de EN_ATTENTE à VALIDEE
     */
    OffreResponse validerOffre(Long id);
    
    /**
     * Récupère toutes les offres d'une entreprise
     */
    List<OffreResponse> getOffresByEntreprise(String emailEntreprise);
    
    /**
     * Recherche d'offres par titre
     */
    List<OffreResponse> searchOffres(String titre);
    
    /**
     * Récupère toutes les offres (admin uniquement)
     * Inclut toutes les offres, y compris celles en attente
     */
    List<OffreResponse> getAllOffres();
    
    /**
     * Marque automatiquement les offres expirées (RG06)
     * Business Rule RG06: Une offre expirée n'est plus consultable
     * Cette méthode doit être appelée périodiquement (scheduler) ou manuellement
     */
    void marquerOffresExpirees();
}
