package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.MaterialComercial;
import com.expogest.expogest.servicios.MaterialComercialService;
import com.expogest.expogest.servicios.SolicitudStandService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/material-comercial")
public class MaterialComercialController {
    
    @Autowired
    private MaterialComercialService materialService;
    
    @Autowired
    private SolicitudStandService solicitudService;
    
    @GetMapping
    public String verMiMaterial(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        String expositorId = (String) session.getAttribute("usuarioId");
        
        // Solo EXPOSITOR puede gestionar su material comercial
        if (!"EXPOSITOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los expositores pueden gestionar material comercial");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        MaterialComercial material = materialService.obtenerPorExpositor(expositorId)
            .orElse(new MaterialComercial());
        material.setExpositorId(expositorId);
        
        model.addAttribute("material", material);
        return "expositor/materialComercial";
    }
    
    @PostMapping("/guardar")
    public String guardarMaterial(@ModelAttribute MaterialComercial material,
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
            materialService.guardar(material);
            redirectAttributes.addFlashAttribute("mensaje", "✓ Material comercial guardado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el material");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        
        return "redirect:/material-comercial";
    }
    
    @GetMapping("/ver/{standId}")
    public String verMaterialPorStand(@PathVariable String standId, Model model, HttpSession session) {
        MaterialComercial material = materialService.obtenerPorStand(standId)
            .orElse(null);
        
        String rol = (String) session.getAttribute("rol");
        model.addAttribute("material", material);
        model.addAttribute("rol", rol);
        
        // Si es evaluador, redirigir a la vista de evaluador
        if ("EVALUADOR".equals(rol)) {
            return "redirect:/evaluador/material-comercial/" + standId;
        }
        
        return "visitante/materialComercial";
    }
}
