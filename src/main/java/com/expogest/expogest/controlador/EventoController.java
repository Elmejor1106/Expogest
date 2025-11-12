package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Evento.EstadoEvento;
import com.expogest.expogest.servicios.EventoService;
import com.expogest.expogest.servicios.StandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private StandService standService;

    @GetMapping
    public String listarEventos(Model model) {
        model.addAttribute("eventos", eventoService.obtenerTodos());
        return "evento/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoEvento(Model model) {
        Evento evento = new Evento();
        evento.setEstado(EstadoEvento.PLANIFICACION);
        model.addAttribute("evento", evento);
        model.addAttribute("estados", EstadoEvento.values());
        return "evento/form";
    }

    @PostMapping("/guardar")
    public String guardarEvento(@ModelAttribute Evento evento, RedirectAttributes redirectAttributes) {
        try {
            eventoService.guardar(evento);
            redirectAttributes.addFlashAttribute("mensaje", "Evento guardado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/eventos/nuevo";
        }
        return "redirect:/eventos";
    }

    // Maneja accesos a /eventos/editar/ sin ID
    @GetMapping("/editar")
    public String editarSinId(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", "Debe seleccionar un evento para editar");
        redirectAttributes.addFlashAttribute("tipo", "warning");
        return "redirect:/eventos";
    }

    @GetMapping("/editar/{id}")
    public String editarEvento(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        if (id == null || id.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "ID de evento inválido");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/eventos";
        }
        
        Evento evento = eventoService.obtenerPorId(id).orElse(null);
        if (evento == null) {
            redirectAttributes.addFlashAttribute("mensaje", "Evento no encontrado");
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/eventos";
        }
        
        model.addAttribute("evento", evento);
        model.addAttribute("estados", EstadoEvento.values());
        return "evento/form";
    }

    // Maneja accesos a /eventos/eliminar/ sin ID
    @GetMapping("/eliminar")
    public String eliminarSinId(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", "Debe seleccionar un evento para eliminar");
        redirectAttributes.addFlashAttribute("tipo", "warning");
        return "redirect:/eventos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarEvento(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            eventoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Evento eliminado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/eventos";
    }

    @GetMapping("/{id}/stands")
    public String verStandsDelEvento(@PathVariable String id, Model model) {
        Evento evento = eventoService.obtenerPorId(id).orElse(null);
        if (evento == null) {
            return "redirect:/eventos";
        }
        model.addAttribute("evento", evento);
        model.addAttribute("stands", eventoService.obtenerStandsDelEvento(id));
        model.addAttribute("standsDisponibles", standService.obtenerDisponibles());
        return "evento/stands";
    }

    @PostMapping("/{eventoId}/stands/{standId}/asociar")
    public String asociarStand(@PathVariable String eventoId, @PathVariable String standId, 
                               RedirectAttributes redirectAttributes) {
        try {
            eventoService.asociarStand(eventoId, standId);
            redirectAttributes.addFlashAttribute("mensaje", "Stand asociado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/eventos/" + eventoId + "/stands";
    }

    @PostMapping("/{eventoId}/stands/{standId}/desasociar")
    public String desasociarStand(@PathVariable String eventoId, @PathVariable String standId,
                                  RedirectAttributes redirectAttributes) {
        try {
            eventoService.desasociarStand(eventoId, standId);
            redirectAttributes.addFlashAttribute("mensaje", "Stand desasociado exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al desasociar el stand");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        return "redirect:/eventos/" + eventoId + "/stands";
    }
}