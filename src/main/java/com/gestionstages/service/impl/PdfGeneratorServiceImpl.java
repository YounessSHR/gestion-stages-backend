package com.gestionstages.service.impl;

import com.gestionstages.model.entity.Convention;
import com.gestionstages.service.PdfGeneratorService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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
 * Service implementation for generating professional PDF documents from conventions.
 */
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Value("${upload.conventions.dir}")
    private String conventionsDir;

    // Professional color scheme
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(30, 64, 175); // Blue-800
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(59, 130, 246); // Blue-500
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(16, 185, 129); // Green-500
    private static final DeviceRgb LIGHT_BG = new DeviceRgb(249, 250, 251); // Gray-50
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(229, 231, 235); // Gray-200

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

            // Set margins
            document.setMargins(50, 50, 60, 50);

            generatePdfContent(document, convention);
        } catch (IOException e) {
            throw new Exception("Error generating PDF: " + e.getMessage(), e);
        }

        // Return relative path
        return fileName;
    }

    /**
     * Generates professional PDF content for the convention.
     */
    private void generatePdfContent(Document document, Convention convention) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale.FRENCH);
        DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm", java.util.Locale.FRENCH);

        // Header with colored background
        Div header = new Div()
                .setBackgroundColor(PRIMARY_COLOR)
                .setPadding(20)
                .setMarginBottom(30);
        
        Paragraph title = new Paragraph("CONVENTION DE STAGE")
                .setFontSize(28)
                .setBold()
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setMargin(0);
        
        Paragraph subtitle = new Paragraph("Document Officiel")
                .setFontSize(12)
                .setFontColor(new DeviceRgb(200, 200, 200))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5);
        
        header.add(title);
        header.add(subtitle);
        document.add(header);

        // Convention Number Badge
        Div badgeDiv = new Div()
                .setBackgroundColor(LIGHT_BG)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(15)
                .setMarginBottom(20);
        
        Paragraph badge = new Paragraph("N° Convention: " + convention.getId())
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMargin(0);
        
        badgeDiv.add(badge);
        document.add(badgeDiv);

        // Main content in two columns
        Table mainTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(20);

        // Left column - Convention Info
        Cell leftCell = new Cell()
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(15)
                .setBackgroundColor(LIGHT_BG);
        
        leftCell.add(createSectionTitle("Informations de la Convention"));
        leftCell.add(createInfoRow("Date de génération", convention.getDateGeneration().format(datetimeFormatter)));
        leftCell.add(createInfoRow("Statut", convention.getStatut().toString()));
        
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
            convention.getDateDebutStage(), 
            convention.getDateFinStage()
        );
        leftCell.add(createInfoRow("Durée", daysBetween + " jours"));
        
        mainTable.addCell(leftCell);

        // Right column - Stage Period
        Cell rightCell = new Cell()
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(15)
                .setBackgroundColor(LIGHT_BG);
        
        rightCell.add(createSectionTitle("Période du Stage"));
        rightCell.add(createInfoRow("Date de début", convention.getDateDebutStage().format(dateFormatter)));
        rightCell.add(createInfoRow("Date de fin", convention.getDateFinStage().format(dateFormatter)));
        
        mainTable.addCell(rightCell);
        document.add(mainTable);

        // Student Information Section
        if (convention.getCandidature() != null && convention.getCandidature().getEtudiant() != null) {
            document.add(createSectionHeader("Informations de l'Étudiant", SECONDARY_COLOR));
            
            Table studentTable = createInfoTable();
            studentTable.addCell(createTableCell("Nom complet", 
                convention.getCandidature().getEtudiant().getPrenom() + " " + 
                convention.getCandidature().getEtudiant().getNom(), true));
            studentTable.addCell(createTableCell("Email", 
                convention.getCandidature().getEtudiant().getEmail(), false));
            
            if (convention.getCandidature().getEtudiant().getNiveau() != null) {
                studentTable.addCell(createTableCell("Niveau", 
                    convention.getCandidature().getEtudiant().getNiveau(), true));
            }
            if (convention.getCandidature().getEtudiant().getFiliere() != null) {
                studentTable.addCell(createTableCell("Filière", 
                    convention.getCandidature().getEtudiant().getFiliere(), false));
            }
            if (convention.getCandidature().getEtudiant().getTelephone() != null) {
                studentTable.addCell(createTableCell("Téléphone", 
                    convention.getCandidature().getEtudiant().getTelephone(), true));
            }
            
            document.add(studentTable);
        }

        // Company Information Section
        if (convention.getCandidature() != null && convention.getCandidature().getOffre() != null 
            && convention.getCandidature().getOffre().getEntreprise() != null) {
            document.add(createSectionHeader("Informations de l'Entreprise", SECONDARY_COLOR));
            
            Table companyTable = createInfoTable();
            companyTable.addCell(createTableCell("Nom de l'entreprise", 
                convention.getCandidature().getOffre().getEntreprise().getNomEntreprise(), true));
            companyTable.addCell(createTableCell("Email", 
                convention.getCandidature().getOffre().getEntreprise().getEmail(), false));
            
            if (convention.getCandidature().getOffre().getEntreprise().getSecteurActivite() != null) {
                companyTable.addCell(createTableCell("Secteur d'activité", 
                    convention.getCandidature().getOffre().getEntreprise().getSecteurActivite(), true));
            }
            if (convention.getCandidature().getOffre().getEntreprise().getAdresse() != null) {
                companyTable.addCell(createTableCell("Adresse", 
                    convention.getCandidature().getOffre().getEntreprise().getAdresse(), false));
            }
            if (convention.getCandidature().getOffre().getEntreprise().getTelephone() != null) {
                companyTable.addCell(createTableCell("Téléphone", 
                    convention.getCandidature().getOffre().getEntreprise().getTelephone(), true));
            }
            
            document.add(companyTable);
        }

        // Offer Information Section
        if (convention.getCandidature() != null && convention.getCandidature().getOffre() != null) {
            document.add(createSectionHeader("Informations de l'Offre", SECONDARY_COLOR));
            
            Table offerTable = createInfoTable();
            offerTable.addCell(createTableCell("Titre", 
                convention.getCandidature().getOffre().getTitre(), true));
            offerTable.addCell(createTableCell("Type", 
                convention.getCandidature().getOffre().getTypeOffre().toString(), false));
            
            if (convention.getCandidature().getOffre().getDuree() != null) {
                offerTable.addCell(createTableCell("Durée", 
                    convention.getCandidature().getOffre().getDuree() + " mois", true));
            }
            if (convention.getCandidature().getOffre().getRemuneration() != null) {
                offerTable.addCell(createTableCell("Rémunération", 
                    convention.getCandidature().getOffre().getRemuneration() + " €", false));
            }
            
            document.add(offerTable);
        }

        // Signatures Section
        document.add(createSectionHeader("Signatures", ACCENT_COLOR));
        
        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(30);
        
        // Student signature
        Cell studentSigCell = createSignatureCell(
            "Étudiant",
            convention.getSignatureEtudiant(),
            convention.getCandidature() != null && convention.getCandidature().getEtudiant() != null
                ? convention.getCandidature().getEtudiant().getPrenom() + " " + 
                  convention.getCandidature().getEtudiant().getNom()
                : "N/A"
        );
        signatureTable.addCell(studentSigCell);
        
        // Enterprise signature
        Cell enterpriseSigCell = createSignatureCell(
            "Entreprise",
            convention.getSignatureEntreprise(),
            convention.getCandidature() != null && 
            convention.getCandidature().getOffre() != null &&
            convention.getCandidature().getOffre().getEntreprise() != null
                ? convention.getCandidature().getOffre().getEntreprise().getNomEntreprise()
                : "N/A"
        );
        signatureTable.addCell(enterpriseSigCell);
        
        // Administration signature
        Cell adminSigCell = createSignatureCell(
            "Administration",
            convention.getSignatureAdministration(),
            "Service Administratif"
        );
        signatureTable.addCell(adminSigCell);
        
        document.add(signatureTable);

        // Footer
        Div footer = new Div()
                .setBackgroundColor(LIGHT_BG)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setPadding(10)
                .setMarginTop(30);
        
        Paragraph footerText = new Paragraph(
            "Document généré le " + java.time.LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm", java.util.Locale.FRENCH)
            ) + " - Système de Gestion de Stages et Alternances"
        )
                .setFontSize(9)
                .setFontColor(new DeviceRgb(107, 114, 128))
                .setTextAlignment(TextAlignment.CENTER)
                .setMargin(0);
        
        footer.add(footerText);
        document.add(footer);
    }

    /**
     * Creates a section title paragraph.
     */
    private Paragraph createSectionTitle(String title) {
        return new Paragraph(title)
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10)
                .setMarginTop(0);
    }

    /**
     * Creates a section header with colored background.
     */
    private Div createSectionHeader(String title, DeviceRgb color) {
        Div header = new Div()
                .setBackgroundColor(color)
                .setPadding(12)
                .setMarginTop(20)
                .setMarginBottom(10);
        
        Paragraph titlePara = new Paragraph(title)
                .setFontSize(16)
                .setBold()
                .setFontColor(ColorConstants.WHITE)
                .setMargin(0);
        
        header.add(titlePara);
        return header;
    }

    /**
     * Creates an info row.
     */
    private Paragraph createInfoRow(String label, String value) {
        return new Paragraph(label + ": " + value)
                .setFontSize(11)
                .setMarginBottom(5)
                .setMarginTop(0);
    }

    /**
     * Creates an information table.
     */
    private Table createInfoTable() {
        return new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15)
                .setBorder(new SolidBorder(BORDER_COLOR, 1));
    }

    /**
     * Creates a table cell with label and value.
     */
    private Cell createTableCell(String label, String value, boolean isEven) {
        Cell cell = new Cell()
                .setPadding(12)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                .setBackgroundColor(isEven ? LIGHT_BG : ColorConstants.WHITE);
        
        Paragraph labelPara = new Paragraph(label)
                .setFontSize(10)
                .setFontColor(new DeviceRgb(107, 114, 128))
                .setMarginBottom(3)
                .setMarginTop(0)
                .setBold();
        
        Paragraph valuePara = new Paragraph(value != null ? value : "N/A")
                .setFontSize(11)
                .setFontColor(new DeviceRgb(17, 24, 39))
                .setMargin(0);
        
        cell.add(labelPara);
        cell.add(valuePara);
        return cell;
    }

    /**
     * Creates a signature cell.
     */
    private Cell createSignatureCell(String role, boolean signed, String signerName) {
        Cell cell = new Cell()
                .setPadding(15)
                .setBorder(new SolidBorder(BORDER_COLOR, 1))
                .setBackgroundColor(signed ? new DeviceRgb(220, 252, 231) : LIGHT_BG)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        
        Paragraph rolePara = new Paragraph(role)
                .setFontSize(12)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(8)
                .setMarginTop(0);
        
        String statusText = signed ? "✓ Signé" : "✗ Non signé";
        DeviceRgb statusColor = signed ? ACCENT_COLOR : new DeviceRgb(239, 68, 68);
        
        Paragraph statusPara = new Paragraph(statusText)
                .setFontSize(14)
                .setBold()
                .setFontColor(statusColor)
                .setMarginBottom(5)
                .setMarginTop(0);
        
        Paragraph namePara = new Paragraph(signerName)
                .setFontSize(10)
                .setFontColor(new DeviceRgb(107, 114, 128))
                .setMargin(0);
        
        cell.add(rolePara);
        cell.add(statusPara);
        cell.add(namePara);
        
        return cell;
    }

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
