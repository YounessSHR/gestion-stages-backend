package com.gestionstages.service.impl;

import com.gestionstages.model.entity.Convention;
import com.gestionstages.service.PdfGeneratorService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * Service implementation for generating PDF documents from conventions.
 */
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Value("${upload.conventions.dir}")
    private String conventionsDir;

    /**
     * Generates a PDF document for a convention.
     * Uses iText HTML to PDF converter to generate the PDF.
     * 
     * @param convention The convention to generate PDF for
     * @return Path to the generated PDF file (relative to uploads directory)
     * @throws Exception if PDF generation fails
     */
    @Override
    public String generateConventionPdf(Convention convention) throws Exception {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(conventionsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate PDF filename
        String fileName = "convention_" + convention.getId() + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = uploadPath.resolve(fileName);

        // Generate PDF using iText
        try (PdfWriter writer = new PdfWriter(filePath.toFile());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            generatePdfContent(document, convention);
        } catch (IOException e) {
            throw new Exception("Error generating PDF: " + e.getMessage(), e);
        }

        // Return relative path
        return fileName;
    }

    /**
     * Generates PDF content for the convention.
     * 
     * @param document The iText document
     * @param convention The convention
     */
    private void generatePdfContent(Document document, Convention convention) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

        // Header
        Paragraph title = new Paragraph("CONVENTION DE STAGE")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(20);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Convention Info
        document.add(new Paragraph("Informations de la Convention").setBold());
        document.add(new Paragraph("Numéro de convention: " + convention.getId()));
        document.add(new Paragraph("Date de génération: " + convention.getDateGeneration().format(datetimeFormatter)));
        document.add(new Paragraph("Statut: " + convention.getStatut()));
        document.add(new Paragraph("\n"));

        // Stage Dates
        document.add(new Paragraph("Période du Stage").setBold());
        document.add(new Paragraph("Date de début: " + convention.getDateDebutStage().format(dateFormatter)));
        document.add(new Paragraph("Date de fin: " + convention.getDateFinStage().format(dateFormatter)));
        document.add(new Paragraph("\n"));

        // Student Info
        if (convention.getCandidature() != null && convention.getCandidature().getEtudiant() != null) {
            document.add(new Paragraph("Informations de l'Étudiant").setBold());
            document.add(new Paragraph("Nom: " + convention.getCandidature().getEtudiant().getNom()));
            document.add(new Paragraph("Prénom: " + convention.getCandidature().getEtudiant().getPrenom()));
            document.add(new Paragraph("Email: " + convention.getCandidature().getEtudiant().getEmail()));
            if (convention.getCandidature().getEtudiant().getNiveau() != null) {
                document.add(new Paragraph("Niveau: " + convention.getCandidature().getEtudiant().getNiveau()));
            }
            if (convention.getCandidature().getEtudiant().getFiliere() != null) {
                document.add(new Paragraph("Filière: " + convention.getCandidature().getEtudiant().getFiliere()));
            }
            document.add(new Paragraph("\n"));
        }

        // Company Info
        if (convention.getCandidature() != null && convention.getCandidature().getOffre() != null 
            && convention.getCandidature().getOffre().getEntreprise() != null) {
            document.add(new Paragraph("Informations de l'Entreprise").setBold());
            document.add(new Paragraph("Nom de l'entreprise: " + convention.getCandidature().getOffre().getEntreprise().getNomEntreprise()));
            document.add(new Paragraph("Email: " + convention.getCandidature().getOffre().getEntreprise().getEmail()));
            if (convention.getCandidature().getOffre().getEntreprise().getSecteurActivite() != null) {
                document.add(new Paragraph("Secteur d'activité: " + convention.getCandidature().getOffre().getEntreprise().getSecteurActivite()));
            }
            document.add(new Paragraph("\n"));
        }

        // Offer Info
        if (convention.getCandidature() != null && convention.getCandidature().getOffre() != null) {
            document.add(new Paragraph("Informations de l'Offre").setBold());
            document.add(new Paragraph("Titre: " + convention.getCandidature().getOffre().getTitre()));
            document.add(new Paragraph("Type: " + convention.getCandidature().getOffre().getTypeOffre()));
            document.add(new Paragraph("\n"));
        }

        // Signatures
        document.add(new Paragraph("Signatures").setBold());
        document.add(new Paragraph("Signature Étudiant: " + (convention.getSignatureEtudiant() ? "✓ Signé" : "Non signé")));
        document.add(new Paragraph("Signature Entreprise: " + (convention.getSignatureEntreprise() ? "✓ Signé" : "Non signé")));
        document.add(new Paragraph("Signature Administration: " + (convention.getSignatureAdministration() ? "✓ Signé" : "Non signé")));
    }

    /**
     * Retrieves a PDF file as a resource.
     * 
     * @param fileName The PDF file name
     * @return Resource containing the PDF file
     */
    @Override
    public Resource getPdfResource(String fileName) {
        try {
            Path filePath = Paths.get(conventionsDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading file: " + fileName, e);
        }
    }
}

