package com.gestionstages.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Service interface for file storage operations.
 * Handles file upload, download, and deletion.
 */
public interface FileStorageService {
    
    /**
     * Stores a file and returns the filename.
     * 
     * @param file The file to store
     * @param directory The directory to store the file in (e.g., "cv", "conventions")
     * @return The generated filename
     * @throws Exception if file storage fails
     */
    String storeFile(MultipartFile file, String directory) throws Exception;
    
    /**
     * Loads a file as a Resource.
     * 
     * @param fileName The filename
     * @param directory The directory where the file is stored
     * @return Resource containing the file
     * @throws Exception if file loading fails
     */
    Resource loadFileAsResource(String fileName, String directory) throws Exception;
    
    /**
     * Deletes a file.
     * 
     * @param fileName The filename to delete
     * @param directory The directory where the file is stored
     * @return true if file was deleted, false otherwise
     */
    boolean deleteFile(String fileName, String directory);
    
    /**
     * Gets the path to a file.
     * 
     * @param fileName The filename
     * @param directory The directory where the file is stored
     * @return Path to the file
     */
    Path getFilePath(String fileName, String directory);
}
