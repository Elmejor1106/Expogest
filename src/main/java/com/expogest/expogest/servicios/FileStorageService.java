package com.expogest.expogest.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento de archivos.", ex);
        }
    }

    /**
     * Almacena un archivo y retorna el nombre del archivo guardado
     */
    public String storeFile(MultipartFile file, String subDirectory) {
        // Sanitizar el nombre del archivo
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Verificar si el archivo está vacío
            if (file.isEmpty()) {
                throw new RuntimeException("No se puede almacenar un archivo vacío: " + originalFileName);
            }
            
            // Verificar si el nombre del archivo contiene caracteres inválidos
            if (originalFileName.contains("..")) {
                throw new RuntimeException("El nombre del archivo contiene una secuencia de ruta inválida: " + originalFileName);
            }
            
            // Generar nombre único para evitar sobrescrituras
            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Crear subdirectorio si no existe
            Path targetLocation = fileStorageLocation.resolve(subDirectory);
            Files.createDirectories(targetLocation);
            
            // Copiar archivo
            Path destinationFile = targetLocation.resolve(newFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return subDirectory + "/" + newFileName;
            
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo " + originalFileName + ". Por favor intente de nuevo.", ex);
        }
    }

    /**
     * Carga un archivo como recurso
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo no encontrado: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Archivo no encontrado: " + fileName, ex);
        }
    }

    /**
     * Elimina un archivo
     */
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo eliminar el archivo: " + fileName, ex);
        }
    }

    /**
     * Valida el tipo de archivo
     */
    public boolean isValidFileType(MultipartFile file, String[] allowedTypes) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        for (String allowedType : allowedTypes) {
            if (contentType.contains(allowedType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida el tamaño del archivo (en MB)
     */
    public boolean isValidFileSize(MultipartFile file, long maxSizeMB) {
        long maxSizeBytes = maxSizeMB * 1024 * 1024;
        return file.getSize() <= maxSizeBytes;
    }
}
