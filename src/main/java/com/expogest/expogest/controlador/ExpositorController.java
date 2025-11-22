package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.MaterialComercial;
import com.expogest.expogest.entidades.SolicitudStand;
import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.servicios.EventoService;
import com.expogest.expogest.servicios.FileStorageService;
import com.expogest.expogest.servicios.MaterialComercialService;
import com.expogest.expogest.servicios.SolicitudStandService;
import com.expogest.expogest.servicios.StandService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ExpositorController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private SolicitudStandService solicitudService;
    
    @Autowired
    private MaterialComercialService materialService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private StandService standService;

    @GetMapping("/expositor/panelExpositor")
    public String panelExpositor(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Obtener estadísticas del expositor
        var estadisticas = solicitudService.obtenerEstadisticasPorExpositor(usuario.getId());
        model.addAttribute("estadisticasSolicitudes", estadisticas);
        
        // Calcular totales y tasa de aprobación
        long total = estadisticas.values().stream().mapToLong(Long::longValue).sum();
        long aprobadas = estadisticas.getOrDefault("aprobadas", 0L);
        double tasaAprobacion = total > 0 ? (aprobadas * 100.0 / total) : 0;
        
        model.addAttribute("totalSolicitudes", total);
        model.addAttribute("tasaAprobacion", String.format("%.1f", tasaAprobacion));
        
        // Eventos disponibles para solicitar
        model.addAttribute("eventosDisponibles", eventoService.obtenerEventosActivos().size());
        
        return "expositor/panelExpositor";
    }

    /**
     * Muestra las solicitudes del expositor (igual que mis-solicitudes)
     */
    @GetMapping("/expositor/solicitudStand")
    public String solicitudStand(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("solicitudes", solicitudService.obtenerPorExpositor(usuario.getId()));
        return "expositor/solicitudStand";
    }

    /**
     * Muestra lista de eventos activos para que el expositor seleccione uno y solicite stand
     */
    @GetMapping("/expositor/solicitudes/nueva")
    public String seleccionarEvento(Model model) {
        model.addAttribute("eventos", eventoService.obtenerEventosActivos());
        return "expositor/nuevaSolicitudStand";
    }
    
    /**
     * Gestionar material comercial del expositor
     */
    @GetMapping("/expositor/material-comercial")
    public String verMaterialComercial(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        String expositorId = (String) session.getAttribute("usuarioId");
        
        if (!"EXPOSITOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los expositores pueden gestionar material comercial");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        // Obtener solicitudes aprobadas del expositor para mostrar stands disponibles
        List<SolicitudStand> solicitudesAprobadas = solicitudService.obtenerPorExpositor(expositorId)
            .stream()
            .filter(SolicitudStand::estaAprobada)
            .toList();
        
        MaterialComercial material = materialService.obtenerPorExpositor(expositorId)
            .orElse(new MaterialComercial());
        material.setExpositorId(expositorId);
        
        model.addAttribute("material", material);
        model.addAttribute("solicitudesAprobadas", solicitudesAprobadas);
        
        // Agregar información del stand actual si existe
        if (material.getStandId() != null) {
            standService.obtenerPorId(material.getStandId()).ifPresent(stand -> {
                model.addAttribute("standActual", stand);
                eventoService.obtenerPorId(stand.getEventoId()).ifPresent(evento -> 
                    model.addAttribute("eventoActual", evento)
                );
            });
        }
        
        return "expositor/materialComercial";
    }
    
    @PostMapping("/expositor/material-comercial/guardar")
    public String guardarMaterialComercial(@ModelAttribute MaterialComercial material,
                                          @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
                                          @RequestParam(value = "catalogoFiles", required = false) List<MultipartFile> catalogoFiles,
                                          @RequestParam(value = "multimediaFiles", required = false) List<MultipartFile> multimediaFiles,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        String expositorId = (String) session.getAttribute("usuarioId");
        
        if (!"EXPOSITOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los expositores pueden gestionar material comercial");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        material.setExpositorId(expositorId);
        
        try {
            // Obtener el stand asignado automáticamente desde solicitudes aprobadas
            List<SolicitudStand> solicitudesAprobadas = solicitudService.obtenerPorExpositor(expositorId)
                .stream()
                .filter(SolicitudStand::estaAprobada)
                .toList();
            
            if (solicitudesAprobadas.isEmpty()) {
                redirectAttributes.addFlashAttribute("mensaje", "Debe tener un stand aprobado para gestionar material comercial");
                redirectAttributes.addFlashAttribute("tipo", "warning");
                return "redirect:/expositor/solicitudStand";
            }
            
            // Si el material no tiene standId o el usuario seleccionó uno específico
            if (material.getStandId() == null || material.getStandId().isEmpty()) {
                // Usar el primer stand aprobado por defecto
                material.setStandId(solicitudesAprobadas.get(0).getStandId());
            }
            
            // Validar que el stand seleccionado pertenece al expositor
            String standSeleccionado = material.getStandId();
            boolean standValido = solicitudesAprobadas.stream()
                .anyMatch(sol -> sol.getStandId().equals(standSeleccionado));
            
            if (!standValido) {
                redirectAttributes.addFlashAttribute("mensaje", "El stand seleccionado no está asignado a usted");
                redirectAttributes.addFlashAttribute("tipo", "danger");
                return "redirect:/expositor/material-comercial";
            }
            
            // Obtener material existente si hay uno
            MaterialComercial materialExistente = materialService.obtenerPorExpositor(expositorId).orElse(null);
            if (materialExistente != null) {
                material.setId(materialExistente.getId());
                // Mantener archivos antiguos si existen
                if (material.getLogoPath() == null && materialExistente.getLogoPath() != null) {
                    material.setLogoPath(materialExistente.getLogoPath());
                }
                if (material.getCatalogoPaths() == null || material.getCatalogoPaths().isEmpty()) {
                    material.setCatalogoPaths(materialExistente.getCatalogoPaths());
                }
                if (material.getMultimediaPaths() == null || material.getMultimediaPaths().isEmpty()) {
                    material.setMultimediaPaths(materialExistente.getMultimediaPaths());
                }
            }
            
            // Procesar archivo de logo
            if (logoFile != null && !logoFile.isEmpty()) {
                if (!fileStorageService.isValidFileType(logoFile, new String[]{"image/png", "image/jpeg", "image/jpg"})) {
                    redirectAttributes.addFlashAttribute("mensaje", "El logo debe ser PNG o JPG");
                    redirectAttributes.addFlashAttribute("tipo", "danger");
                    return "redirect:/expositor/material-comercial";
                }
                if (!fileStorageService.isValidFileSize(logoFile, 5)) { // 5MB máximo
                    redirectAttributes.addFlashAttribute("mensaje", "El logo no puede superar 5MB");
                    redirectAttributes.addFlashAttribute("tipo", "danger");
                    return "redirect:/expositor/material-comercial";
                }
                
                // Eliminar logo anterior si existe
                if (materialExistente != null && materialExistente.getLogoPath() != null) {
                    fileStorageService.deleteFile(materialExistente.getLogoPath());
                }
                
                String logoPath = fileStorageService.storeFile(logoFile, "material-comercial/logos");
                material.setLogoPath(logoPath);
            }
            
            // Procesar archivos de catálogos
            if (catalogoFiles != null && !catalogoFiles.isEmpty()) {
                List<String> catalogoPaths = new ArrayList<>();
                if (materialExistente != null && materialExistente.getCatalogoPaths() != null) {
                    catalogoPaths.addAll(materialExistente.getCatalogoPaths());
                }
                
                for (MultipartFile file : catalogoFiles) {
                    if (!file.isEmpty()) {
                        if (!fileStorageService.isValidFileType(file, new String[]{"application/pdf"})) {
                            redirectAttributes.addFlashAttribute("mensaje", "Los catálogos deben ser PDF");
                            redirectAttributes.addFlashAttribute("tipo", "danger");
                            return "redirect:/expositor/material-comercial";
                        }
                        if (!fileStorageService.isValidFileSize(file, 10)) { // 10MB máximo
                            redirectAttributes.addFlashAttribute("mensaje", "Los catálogos no pueden superar 10MB cada uno");
                            redirectAttributes.addFlashAttribute("tipo", "danger");
                            return "redirect:/expositor/material-comercial";
                        }
                        
                        String catalogoPath = fileStorageService.storeFile(file, "material-comercial/catalogos");
                        catalogoPaths.add(catalogoPath);
                    }
                }
                material.setCatalogoPaths(catalogoPaths);
            }
            
            // Procesar archivos multimedia
            if (multimediaFiles != null && !multimediaFiles.isEmpty()) {
                List<String> multimediaPaths = new ArrayList<>();
                if (materialExistente != null && materialExistente.getMultimediaPaths() != null) {
                    multimediaPaths.addAll(materialExistente.getMultimediaPaths());
                }
                
                for (MultipartFile file : multimediaFiles) {
                    if (!file.isEmpty()) {
                        if (!fileStorageService.isValidFileType(file, new String[]{"image/", "video/"})) {
                            redirectAttributes.addFlashAttribute("mensaje", "Los archivos multimedia deben ser imágenes o videos");
                            redirectAttributes.addFlashAttribute("tipo", "danger");
                            return "redirect:/expositor/material-comercial";
                        }
                        if (!fileStorageService.isValidFileSize(file, 50)) { // 50MB máximo
                            redirectAttributes.addFlashAttribute("mensaje", "Los archivos multimedia no pueden superar 50MB cada uno");
                            redirectAttributes.addFlashAttribute("tipo", "danger");
                            return "redirect:/expositor/material-comercial";
                        }
                        
                        String multimediaPath = fileStorageService.storeFile(file, "material-comercial/multimedia");
                        multimediaPaths.add(multimediaPath);
                    }
                }
                material.setMultimediaPaths(multimediaPaths);
            }
            
            materialService.guardar(material);
            redirectAttributes.addFlashAttribute("mensaje", "✓ Material comercial guardado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el material: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        
        return "redirect:/expositor/material-comercial";
    }
}