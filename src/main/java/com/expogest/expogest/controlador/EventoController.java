package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Evento.EstadoEvento;
import com.expogest.expogest.servicios.EventoService;
import com.expogest.expogest.servicios.StandService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private StandService standService;

    @GetMapping
    public String listarEventos(@RequestParam(required = false) String buscar, Model model) {
        if (buscar != null && !buscar.trim().isEmpty()) {
            model.addAttribute("eventos", eventoService.buscarPorNombre(buscar));
            model.addAttribute("buscar", buscar);
        } else {
            model.addAttribute("eventos", eventoService.obtenerTodos());
        }
        return "evento/lista";
    }

    @GetMapping("/buscar")
    public String buscarEventos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado,
            Model model) {
        
        List<Evento> eventos = eventoService.obtenerTodos();
        
        // Filtrar por nombre
        if (nombre != null && !nombre.trim().isEmpty()) {
            eventos = eventos.stream()
                    .filter(e -> e.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por rango de fechas
        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            LocalDate fechaInicioDate = LocalDate.parse(fechaInicio);
            eventos = eventos.stream()
                    .filter(e -> !e.getFechaInicio().isBefore(fechaInicioDate))
                    .collect(Collectors.toList());
        }
        
        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            LocalDate fechaFinDate = LocalDate.parse(fechaFin);
            eventos = eventos.stream()
                    .filter(e -> !e.getFechaFin().isAfter(fechaFinDate))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por estado
        if (estado != null && !estado.trim().isEmpty()) {
            eventos = eventos.stream()
                    .filter(e -> e.getEstado().toString().equals(estado))
                    .collect(Collectors.toList());
        }
        
        model.addAttribute("eventos", eventos);
        return "evento/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoEvento(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Validar que solo organizadores puedan crear eventos
        String rol = (String) session.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "⚠️ Solo los organizadores pueden crear eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
        
        Evento evento = new Evento();
        evento.setEstado(EstadoEvento.PLANIFICACION);
        model.addAttribute("evento", evento);
        model.addAttribute("estados", EstadoEvento.values());
        return "evento/form";
    }

    @PostMapping("/guardar")
    public String guardarEvento(@ModelAttribute Evento evento, HttpSession session, RedirectAttributes redirectAttributes) {
        // Validar que solo organizadores puedan guardar eventos
        String rol = (String) session.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "⚠️ Solo los organizadores pueden crear o modificar eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
        
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
    public String editarEvento(@PathVariable String id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Validar que solo organizadores puedan editar eventos
        String rol = (String) session.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "⚠️ Solo los organizadores pueden editar eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
        
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
    public String eliminarEvento(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Validar que solo organizadores puedan eliminar eventos
        String rol = (String) session.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "⚠️ Solo los organizadores pueden eliminar eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
        
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
    public String verStandsDelEvento(@PathVariable String id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Validar que solo organizadores puedan gestionar stands de eventos
        String rol = (String) session.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "⚠️ Solo los organizadores pueden gestionar stands de eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
        
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
                               HttpSession session, RedirectAttributes redirectAttributes) {
        // Validar que solo organizadores puedan asociar stands
        String rol = (String) session.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "⚠️ Solo los organizadores pueden asociar stands a eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
        
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
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        // Validar que solo organizadores puedan desasociar stands
        String rol = (String) session.getAttribute("rol");
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "⚠️ Solo los organizadores pueden desasociar stands de eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
        
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