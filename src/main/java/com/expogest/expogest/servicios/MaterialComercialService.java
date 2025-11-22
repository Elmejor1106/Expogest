package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.MaterialComercial;
import com.expogest.expogest.repositorio.MaterialComercialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialComercialService {
    
    @Autowired
    private MaterialComercialRepository materialRepository;
    
    public MaterialComercial guardar(MaterialComercial material) {
        return materialRepository.save(material);
    }
    
    public Optional<MaterialComercial> obtenerPorId(String id) {
        return materialRepository.findById(id);
    }
    
    public Optional<MaterialComercial> obtenerPorExpositor(String expositorId) {
        return materialRepository.findByExpositorId(expositorId);
    }
    
    public Optional<MaterialComercial> obtenerPorStand(String standId) {
        return materialRepository.findByStandId(standId);
    }
    
    public List<MaterialComercial> obtenerTodos() {
        return materialRepository.findAll();
    }
    
    public void eliminar(String id) {
        materialRepository.deleteById(id);
    }
    
    public MaterialComercial agregarCatalogo(String materialId, String catalogoUrl) {
        MaterialComercial material = obtenerPorId(materialId)
            .orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));
        
        material.getCatalogoUrls().add(catalogoUrl);
        return materialRepository.save(material);
    }
    
    public MaterialComercial agregarMultimedia(String materialId, String multimediaUrl) {
        MaterialComercial material = obtenerPorId(materialId)
            .orElseThrow(() -> new IllegalArgumentException("Material no encontrado"));
        
        material.getMultimediaUrls().add(multimediaUrl);
        return materialRepository.save(material);
    }
}
