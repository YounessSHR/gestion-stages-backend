package com.gestionstages.service;

import com.gestionstages.model.entity.Convention;
import org.springframework.core.io.Resource;

/**
 * Service for generating PDF documents from conventions.
 */
public interface PdfGeneratorService {
    
    /**
     * Generates a PDF document for a convention.
     * 
     * @param convention The convention to generate PDF for
     * @return Path to the generated PDF file
     * @throws Exception if PDF generation fails
     */
    String generateConventionPdf(Convention convention) throws Exception;
    
    /**
     * Retrieves a PDF file as a resource.
     * 
     * @param fileName The PDF file name
     * @return Resource containing the PDF file
     */
    Resource getPdfResource(String fileName);
}
