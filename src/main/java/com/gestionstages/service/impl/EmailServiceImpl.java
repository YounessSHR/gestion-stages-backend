package com.gestionstages.service.impl;

import com.gestionstages.model.entity.*;
import com.gestionstages.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:Gestion de Stages et Alternances}")
    private String appName;

    @Override
    @Async("taskExecutor")
    public void sendCandidatureAcceptee(Candidature candidature) {
        try {
            Etudiant etudiant = candidature.getEtudiant();
            OffreStage offre = candidature.getOffre();
            Entreprise entreprise = offre.getEntreprise();

            Context context = new Context(Locale.FRENCH);
            context.setVariable("etudiantNom", etudiant.getPrenom() + " " + etudiant.getNom());
            context.setVariable("offreTitre", offre.getTitre());
            context.setVariable("entrepriseNom", entreprise.getNomEntreprise());
            context.setVariable("typeOffre", offre.getTypeOffre().name());
            context.setVariable("dateDebut", offre.getDateDebut() != null ? 
                offre.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");
            context.setVariable("dateFin", offre.getDateFin() != null ? 
                offre.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");

            String htmlContent = templateEngine.process("email/candidature-acceptee", context);
            sendEmail(etudiant.getEmail(), "Candidature Acceptée - " + offre.getTitre(), htmlContent);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de candidature acceptée", e);
        }
    }

    @Override
    @Async("taskExecutor")
    public void sendCandidatureRefusee(Candidature candidature, String commentaire) {
        try {
            Etudiant etudiant = candidature.getEtudiant();
            OffreStage offre = candidature.getOffre();
            Entreprise entreprise = offre.getEntreprise();

            Context context = new Context(Locale.FRENCH);
            context.setVariable("etudiantNom", etudiant.getPrenom() + " " + etudiant.getNom());
            context.setVariable("offreTitre", offre.getTitre());
            context.setVariable("entrepriseNom", entreprise.getNomEntreprise());
            context.setVariable("typeOffre", offre.getTypeOffre().name());
            context.setVariable("commentaire", commentaire != null ? commentaire : "");

            String htmlContent = templateEngine.process("email/candidature-refusee", context);
            sendEmail(etudiant.getEmail(), "Candidature Refusée - " + offre.getTitre(), htmlContent);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de candidature refusée", e);
        }
    }

    @Override
    @Async("taskExecutor")
    public void sendConventionCreee(Convention convention) {
        try {
            Candidature candidature = convention.getCandidature();
            Etudiant etudiant = candidature.getEtudiant();
            Entreprise entreprise = candidature.getOffre().getEntreprise();

            // Send to student
            sendConventionCreeeToUser(convention, etudiant.getEmail(), 
                etudiant.getPrenom() + " " + etudiant.getNom(), "Étudiant");

            // Send to enterprise
            sendConventionCreeeToUser(convention, entreprise.getEmail(), 
                entreprise.getNomEntreprise(), "Entreprise");

            // Send to admin (if needed, get admin emails from repository)
            // For now, we'll skip admin notification for convention creation
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de convention créée", e);
        }
    }

    private void sendConventionCreeeToUser(Convention convention, String email, String nom, String role) {
        try {
            Candidature candidature = convention.getCandidature();
            Etudiant etudiant = candidature.getEtudiant();
            Entreprise entreprise = candidature.getOffre().getEntreprise();

            Context context = new Context(Locale.FRENCH);
            context.setVariable("nom", nom);
            context.setVariable("etudiantNom", etudiant.getPrenom() + " " + etudiant.getNom());
            context.setVariable("entrepriseNom", entreprise.getNomEntreprise());
            context.setVariable("dateDebut", convention.getDateDebutStage() != null ? 
                convention.getDateDebutStage().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");
            context.setVariable("dateFin", convention.getDateFinStage() != null ? 
                convention.getDateFinStage().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");

            String htmlContent = templateEngine.process("email/convention-creee", context);
            sendEmail(email, "Convention Créée", htmlContent);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de convention créée à " + email, e);
        }
    }

    @Override
    @Async("taskExecutor")
    public void sendConventionSignee(Convention convention, String signataire) {
        try {
            Candidature candidature = convention.getCandidature();
            Etudiant etudiant = candidature.getEtudiant();
            Entreprise entreprise = candidature.getOffre().getEntreprise();

            // Send to all parties except the signer
            if (!convention.getSignatureEtudiant() || !signataire.contains("étudiant")) {
                sendConventionSigneeToUser(convention, etudiant.getEmail(), 
                    etudiant.getPrenom() + " " + etudiant.getNom(), signataire);
            }

            if (!convention.getSignatureEntreprise() || !signataire.contains("entreprise")) {
                sendConventionSigneeToUser(convention, entreprise.getEmail(), 
                    entreprise.getNomEntreprise(), signataire);
            }

            // Admin notification if needed
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de convention signée", e);
        }
    }

    private void sendConventionSigneeToUser(Convention convention, String email, String nom, String signataire) {
        try {
            Candidature candidature = convention.getCandidature();
            Etudiant etudiant = candidature.getEtudiant();
            Entreprise entreprise = candidature.getOffre().getEntreprise();

            Context context = new Context(Locale.FRENCH);
            context.setVariable("nom", nom);
            context.setVariable("signataire", signataire);
            context.setVariable("etudiantNom", etudiant.getPrenom() + " " + etudiant.getNom());
            context.setVariable("entrepriseNom", entreprise.getNomEntreprise());
            context.setVariable("statut", convention.getStatut().name());

            String htmlContent = templateEngine.process("email/convention-signee", context);
            sendEmail(email, "Convention Signée", htmlContent);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de convention signée à " + email, e);
        }
    }

    @Override
    @Async("taskExecutor")
    public void sendConventionComplete(Convention convention) {
        try {
            Candidature candidature = convention.getCandidature();
            Etudiant etudiant = candidature.getEtudiant();
            Entreprise entreprise = candidature.getOffre().getEntreprise();

            // Send to student
            sendConventionCompleteToUser(convention, etudiant.getEmail(), 
                etudiant.getPrenom() + " " + etudiant.getNom());

            // Send to enterprise
            sendConventionCompleteToUser(convention, entreprise.getEmail(), 
                entreprise.getNomEntreprise());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de convention complète", e);
        }
    }

    private void sendConventionCompleteToUser(Convention convention, String email, String nom) {
        try {
            Candidature candidature = convention.getCandidature();
            Etudiant etudiant = candidature.getEtudiant();
            Entreprise entreprise = candidature.getOffre().getEntreprise();

            Context context = new Context(Locale.FRENCH);
            context.setVariable("nom", nom);
            context.setVariable("etudiantNom", etudiant.getPrenom() + " " + etudiant.getNom());
            context.setVariable("entrepriseNom", entreprise.getNomEntreprise());
            context.setVariable("dateDebut", convention.getDateDebutStage() != null ? 
                convention.getDateDebutStage().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");
            context.setVariable("dateFin", convention.getDateFinStage() != null ? 
                convention.getDateFinStage().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");

            String htmlContent = templateEngine.process("email/convention-complete", context);
            sendEmail(email, "Convention Complètement Signée", htmlContent);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de convention complète à " + email, e);
        }
    }

    @Override
    @Async("taskExecutor")
    public void sendTuteurAssigne(SuiviStage suiviStage) {
        try {
            Convention convention = suiviStage.getConvention();
            Candidature candidature = convention.getCandidature();
            Etudiant etudiant = candidature.getEtudiant();
            Tuteur tuteur = suiviStage.getTuteur();

            // Send to tutor
            sendTuteurAssigneToUser(suiviStage, tuteur.getEmail(), 
                tuteur.getPrenom() + " " + tuteur.getNom(), "TUTEUR");

            // Send to student
            sendTuteurAssigneToUser(suiviStage, etudiant.getEmail(), 
                etudiant.getPrenom() + " " + etudiant.getNom(), "ETUDIANT");
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de tuteur assigné", e);
        }
    }

    private void sendTuteurAssigneToUser(SuiviStage suiviStage, String email, String nom, String role) {
        try {
            Convention convention = suiviStage.getConvention();
            Candidature candidature = convention.getCandidature();
            Etudiant etudiant = candidature.getEtudiant();
            Tuteur tuteur = suiviStage.getTuteur();

            Context context = new Context(Locale.FRENCH);
            context.setVariable("nom", nom);
            context.setVariable("role", role);
            context.setVariable("etudiantNom", etudiant.getPrenom() + " " + etudiant.getNom());
            context.setVariable("tuteurNom", tuteur.getPrenom() + " " + tuteur.getNom());
            context.setVariable("entrepriseNom", candidature.getOffre().getEntreprise().getNomEntreprise());
            context.setVariable("dateDebut", convention.getDateDebutStage() != null ? 
                convention.getDateDebutStage().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");
            context.setVariable("dateFin", convention.getDateFinStage() != null ? 
                convention.getDateFinStage().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");

            String htmlContent = templateEngine.process("email/tuteur-assigne", context);
            sendEmail(email, "Tuteur Assigné", htmlContent);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de tuteur assigné à " + email, e);
        }
    }

    @Override
    @Async("taskExecutor")
    public void sendOffreValidee(OffreStage offre) {
        try {
            Entreprise entreprise = offre.getEntreprise();

            Context context = new Context(Locale.FRENCH);
            context.setVariable("entrepriseNom", entreprise.getNomEntreprise());
            context.setVariable("offreTitre", offre.getTitre());
            context.setVariable("typeOffre", offre.getTypeOffre().name());
            context.setVariable("dateDebut", offre.getDateDebut() != null ? 
                offre.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");
            context.setVariable("dateFin", offre.getDateFin() != null ? 
                offre.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifiée");

            String htmlContent = templateEngine.process("email/offre-validee", context);
            sendEmail(entreprise.getEmail(), "Offre Validée - " + offre.getTitre(), htmlContent);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email d'offre validée", e);
        }
    }

    @Override
    @Async("taskExecutor")
    public void sendOffreRefusee(OffreStage offre, String raison) {
        try {
            Entreprise entreprise = offre.getEntreprise();

            Context context = new Context(Locale.FRENCH);
            context.setVariable("entrepriseNom", entreprise.getNomEntreprise());
            context.setVariable("offreTitre", offre.getTitre());
            context.setVariable("typeOffre", offre.getTypeOffre().name());
            context.setVariable("raison", raison != null ? raison : "Non spécifiée");

            String htmlContent = templateEngine.process("email/offre-refusee", context);
            sendEmail(entreprise.getEmail(), "Offre Refusée - " + offre.getTitre(), htmlContent);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email d'offre refusée", e);
        }
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, appName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email envoyé avec succès à: " + to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Erreur lors de l'envoi de l'email à " + to, e);
            // Don't throw exception to avoid breaking the main flow
        }
    }
}
