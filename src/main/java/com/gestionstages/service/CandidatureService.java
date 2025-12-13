package com.gestionstages.service;

import com.gestionstages.model.dto.request.CandidatureRequest;
import com.gestionstages.model.dto.response.CandidatureResponse;

import java.util.List;

public interface CandidatureService {
    
    /**
     * Crée une nouvelle candidature (étudiant uniquement)
     * RG01: Un étudiant ne peut postuler qu'une fois à la même offre
     */
    CandidatureResponse createCandidature(CandidatureRequest request, String emailEtudiant);
    
    /**
     * Récupère une candidature par son ID
     */
    CandidatureResponse getCandidatureById(Long id);
    
    /**
     * Récupère toutes les candidatures d'un étudiant
     */
    List<CandidatureResponse> getCandidaturesByEtudiant(String emailEtudiant);
    
    /**
     * Récupère toutes les candidatures pour une offre (entreprise propriétaire uniquement)
     */
    List<CandidatureResponse> getCandidaturesByOffre(Long offreId, String emailEntreprise);
    
    /**
     * Accepte une candidature (entreprise propriétaire uniquement)
     * RG03: Une candidature acceptée déclenche la génération d'une convention
     */
    CandidatureResponse accepterCandidature(Long id, String emailEntreprise);
    
    /**
     * Refuse une candidature (entreprise propriétaire uniquement)
     */
    CandidatureResponse refuserCandidature(Long id, String emailEntreprise, String commentaire);
    
    /**
     * Supprime une candidature (étudiant propriétaire uniquement)
     */
    void deleteCandidature(Long id, String emailEtudiant);
}
