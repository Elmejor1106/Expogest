package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.entidades.Stand.EstadoStand;
import com.expogest.expogest.servicios.StandService;
import com.expogest.expogest.servicios.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stands")
public class StandController {

    @Autowired
    private StandService standService;

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public String listarStands(Model model) {
        model.addAttribute("stands", standService.obtenerTodos());
        return "organizador/stands";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Stand stand = new Stand();
        stand.setEstado(EstadoStand.DISPONIBLE);
        model.addAttribute("stand", stand);
        model.addAttribute("estados", EstadoStand.values());
        return "organizador/nuevoStand";
    }

    @PostMapping("/guardar")
    public String guardarStand(@ModelAttribute Stand stand, RedirectAttributes redirectAttributes) {
        try {
            standService.guardar(stand);
            redirectAttributes.addFlashAttribute("mensaje", "Stand guardado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/stands/nuevo";
        }
        return "redirect:/stands";
    }

    // Maneja accesos a /stands/editar/ sin ID
    @GetMapping("/editar")
    public String editarSinId(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", "Debe seleccionar un stand para editar");
        redirectAttributes.addFlashAttribute("tipo", "warning");
        return "redirect:/stands";
    }
    
    @GetMapping("/editar/{id}")
    public String editarStand(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        if (id == null || id.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "ID de stand inválido");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/stands";
        }
        
        Stand stand = standService.obtenerPorId(id).orElse(null);
        if (stand == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Stand no encontrado");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/stands";
        }
        
        model.addAttribute("stand", stand);
        model.addAttribute("estados", EstadoStand.values());
        return "organizador/nuevoStand";
    }

    // Maneja accesos a /stands/eliminar/ sin ID
    @GetMapping("/eliminar")
    public String eliminarSinId(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", "Debe seleccionar un stand para eliminar");
        redirectAttributes.addFlashAttribute("tipo", "warning");
        return "redirect:/stands";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarStand(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            standService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Stand eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/stands";
    }

    @GetMapping("/disponibles")
    public String listarStandsDisponibles(Model model) {
        model.addAttribute("stands", standService.obtenerDisponibles());
        model.addAttribute("eventos", eventoService.obtenerEventosActivos());
        return "organizador/standsDisponibles";
    }

    @PostMapping("/{standId}/asignar-expositor/{expositorId}")
    public String asignarExpositor(@PathVariable String standId, @PathVariable String expositorId,
                                   RedirectAttributes redirectAttributes) {
        try {
            standService.asignarExpositor(standId, expositorId);
            redirectAttributes.addFlashAttribute("mensaje", "Expositor asignado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/stands";
    }

    @PostMapping("/{standId}/liberar")
    public String liberarStand(@PathVariable String standId, RedirectAttributes redirectAttributes) {
        standService.liberarStand(standId);
        redirectAttributes.addFlashAttribute("mensaje", "Stand liberado exitosamente");
        redirectAttributes.addFlashAttribute("tipo", "success");
        return "redirect:/stands";
    }
}