package com.gestionstages.service.impl;

import com.gestionstages.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service implementation for file storage operations.
 * Handles file upload, download, and deletion with security checks.
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Value("${upload.dir}")
    private String uploadDir;

    /**
     * Stores a file and returns the generated filename.
     * Uses UUID to prevent filename conflicts and security issues.
     * 
     * @param file The file to store
     * @param directory The subdirectory (e.g., "cv", "conventions")
     * @return The generated filename
     * @throws Exception if file storage fails
     */
    @Override
    public String storeFile(MultipartFile file, String directory) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename: UUID_originalFilename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Original filename is empty");
        }

        // Sanitize filename
        String sanitizedFilename = sanitizeFilename(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "_" + sanitizedFilename;

        // Store file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        logger.info("File stored successfully: {}", uniqueFilename);
        return uniqueFilename;
    }

    /**
     * Loads a file as a Resource.
     * 
     * @param fileName The filename
     * @param directory The subdirectory
     * @return Resource containing the file
     * @throws Exception if file loading fails
     */
    @Override
    public Resource loadFileAsResource(String fileName, String directory) throws Exception {
        try {
            Path filePath = getFilePath(fileName, directory).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + fileName, ex);
        }
    }

    /**
     * Deletes a file.
     * 
     * @param fileName The filename to delete
     * @param directory The subdirectory
     * @return true if file was deleted, false otherwise
     */
    @Override
    public boolean deleteFile(String fileName, String directory) {
        try {
            Path filePath = getFilePath(fileName, directory);
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            logger.error("Error deleting file: {}", fileName, ex);
            return false;
        }
    }

    /**
     * Gets the path to a file.
     * 
     * @param fileName The filename
     * @param directory The subdirectory
     * @return Path to the file
     */
    @Override
    public Path getFilePath(String fileName, String directory) {
        return Paths.get(uploadDir, directory, fileName);
    }

    /**
     * Sanitizes a filename to prevent path traversal attacks.
     * 
     * @param filename The original filename
     * @return Sanitized filename
     */
    private String sanitizeFilename(String filename) {
        // Remove path separators and other dangerous characters
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    /**
     * Gets the file extension from a filename.
     * 
     * @param filename The filename
     * @return The file extension (without dot) or empty string
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
}
