package com.gestionstages.service;

import com.gestionstages.model.entity.*;

public interface EmailService {
    
    void sendCandidatureAcceptee(Candidature candidature);
    
    void sendCandidatureRefusee(Candidature candidature, String commentaire);
    
    void sendConventionCreee(Convention convention);
    
    void sendConventionSignee(Convention convention, String signataire);
    
    void sendConventionComplete(Convention convention);
    
    void sendTuteurAssigne(SuiviStage suiviStage);
    
    void sendOffreValidee(OffreStage offre);
    
    void sendOffreRefusee(OffreStage offre, String raison);
}
