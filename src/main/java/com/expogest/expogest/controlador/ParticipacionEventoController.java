package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.ParticipacionEvento;
import com.expogest.expogest.servicios.ParticipacionEventoService;
import com.expogest.expogest.servicios.EventoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/participaciones")
public class ParticipacionEventoController {
    
    @Autowired
    private ParticipacionEventoService participacionService;
    
    @Autowired
    private EventoService eventoService;
    
    @GetMapping
    public String listarMisInscripciones(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        String visitanteId = (String) session.getAttribute("usuarioId");
        
        if (!"VISITANTE".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los visitantes pueden ver sus inscripciones");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        var participaciones = participacionService.obtenerPorVisitante(visitanteId);
        model.addAttribute("participaciones", participaciones);
        
        // Crear mapa de eventos para mostrar nombres
        java.util.Map<String, com.expogest.expogest.entidades.Evento> eventosMap = new java.util.HashMap<>();
        for (var p : participaciones) {
            eventoService.obtenerPorId(p.getEventoId()).ifPresent(evento -> 
                eventosMap.put(p.getEventoId(), evento)
            );
        }
        model.addAttribute("eventosMap", eventosMap);
        
        return "participacion/misInscripciones";
    }
    
    @PostMapping("/inscribir/{eventoId}")
    public String inscribirEnEvento(@PathVariable String eventoId, 
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        String visitanteId = (String) session.getAttribute("usuarioId");
        
        if (!"VISITANTE".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los visitantes pueden inscribirse en eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
        
        try {
            participacionService.inscribir(eventoId, visitanteId);
            redirectAttributes.addFlashAttribute("mensaje", "✓ Inscripción confirmada exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "warning");
        }
        
        return "redirect:/visitante/panelVisitante";
    }
    
    @PostMapping("/cancelar/{participacionId}")
    public String cancelarInscripcion(@PathVariable String participacionId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        
        if (!"VISITANTE".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los visitantes pueden cancelar inscripciones");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        try {
            participacionService.cancelarInscripcion(participacionId);
            redirectAttributes.addFlashAttribute("mensaje", "Inscripción cancelada");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cancelar la inscripción");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        
        return "redirect:/participaciones";
    }
}
