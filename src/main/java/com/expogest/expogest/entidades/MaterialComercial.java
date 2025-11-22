package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "materiales_comerciales")
public class MaterialComercial {
    
    @Id
    private String id;
    private String expositorId;
    private String standId;
    private String descripcion;
    private String logoUrl; // URL del logotipo
    private String logoPath; // Ruta del archivo del logotipo subido
    private List<String> catalogoUrls; // URLs de catálogos PDF
    private List<String> catalogoPaths; // Rutas de archivos PDF subidos
    private List<String> multimediaUrls; // URLs de videos e imágenes
    private List<String> multimediaPaths; // Rutas de archivos multimedia subidos
    
    // Constructores
    public MaterialComercial() {
        this.catalogoUrls = new ArrayList<>();
        this.multimediaUrls = new ArrayList<>();
        this.catalogoPaths = new ArrayList<>();
        this.multimediaPaths = new ArrayList<>();
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getExpositorId() {
        return expositorId;
    }
    
    public void setExpositorId(String expositorId) {
        this.expositorId = expositorId;
    }
    
    public String getStandId() {
        return standId;
    }
    
    public void setStandId(String standId) {
        this.standId = standId;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    
    public List<String> getCatalogoUrls() {
        return catalogoUrls;
    }
    
    public void setCatalogoUrls(List<String> catalogoUrls) {
        this.catalogoUrls = catalogoUrls;
    }
    
    public List<String> getMultimediaUrls() {
        return multimediaUrls;
    }
    
    public void setMultimediaUrls(List<String> multimediaUrls) {
        this.multimediaUrls = multimediaUrls;
    }
    
    public String getLogoPath() {
        return logoPath;
    }
    
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
    
    public List<String> getCatalogoPaths() {
        return catalogoPaths;
    }
    
    public void setCatalogoPaths(List<String> catalogoPaths) {
        this.catalogoPaths = catalogoPaths;
    }
    
    public List<String> getMultimediaPaths() {
        return multimediaPaths;
    }
    
    public void setMultimediaPaths(List<String> multimediaPaths) {
        this.multimediaPaths = multimediaPaths;
    }
}
