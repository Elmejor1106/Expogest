package com.expogest.expogest.controlador;

import com.expogest.expogest.servicios.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Descarga o visualiza archivos subidos
     * Soporta rutas con múltiples niveles como: /uploads/material-comercial/logos/archivo.jpg
     */
    @GetMapping("/uploads/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        // Obtener la ruta completa después de /uploads/
        String requestPath = request.getRequestURI().substring("/uploads/".length());
        
        // Cargar archivo como recurso
        Resource resource = fileStorageService.loadFileAsResource(requestPath);

        // Determinar el tipo de contenido del archivo
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // No hacer nada
        }

        // Si no se pudo determinar el tipo, usar genérico
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
