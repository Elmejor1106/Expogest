package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.ActividadCronograma;
import com.expogest.expogest.servicios.ActividadCronogramaService;
import com.expogest.expogest.servicios.EventoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/cronograma")
public class CronogramaController {

    @Autowired
    private ActividadCronogramaService cronogramaService;
    
    @Autowired
    private EventoService eventoService;
    
    // Seleccionar evento para gestionar cronograma (Organizador)
    @GetMapping("/seleccionar-evento")
    public String seleccionarEvento(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los organizadores pueden gestionar cronogramas");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        model.addAttribute("eventos", eventoService.obtenerTodos());
        return "organizador/seleccionarEventoCronograma";
    }
    
    // CRUD para organizador
    @GetMapping("/evento/{eventoId}")
    public String gestionarCronograma(@PathVariable String eventoId,
                                     HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        
        if (!"ORGANIZADOR".equals(rol)) {
            // Vista pública para otros roles
            return "redirect:/cronograma/ver/" + eventoId;
        }
        
        model.addAttribute("evento", eventoService.obtenerPorId(eventoId).orElse(null));
        model.addAttribute("actividades", cronogramaService.obtenerPorEvento(eventoId));
        model.addAttribute("tipos", ActividadCronograma.TipoActividad.values());
        return "organizador/cronograma";
    }
    
    @GetMapping("/nuevo/{eventoId}")
    public String nuevaActividad(@PathVariable String eventoId,
                                 HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los organizadores pueden crear actividades");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/cronograma/ver/" + eventoId;
        }
        
        ActividadCronograma actividad = new ActividadCronograma();
        actividad.setEventoId(eventoId);
        
        model.addAttribute("actividad", actividad);
        model.addAttribute("evento", eventoService.obtenerPorId(eventoId).orElse(null));
        model.addAttribute("tipos", ActividadCronograma.TipoActividad.values());
        return "organizador/cronogramaForm";
    }
    
    @PostMapping("/guardar")
    public String guardarActividad(@ModelAttribute ActividadCronograma actividad,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los organizadores pueden crear actividades");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        try {
            cronogramaService.guardar(actividad);
            redirectAttributes.addFlashAttribute("mensaje", "✓ Actividad guardada exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            return "redirect:/cronograma/nuevo/" + actividad.getEventoId();
        }
        
        return "redirect:/cronograma/evento/" + actividad.getEventoId();
    }
    
    @GetMapping("/editar/{id}")
    public String editarActividad(@PathVariable String id,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los organizadores pueden editar actividades");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        ActividadCronograma actividad = cronogramaService.obtenerPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
        
        model.addAttribute("actividad", actividad);
        model.addAttribute("evento", eventoService.obtenerPorId(actividad.getEventoId()).orElse(null));
        model.addAttribute("tipos", ActividadCronograma.TipoActividad.values());
        return "organizador/cronogramaForm";
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarActividad(@PathVariable String id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        
        if (!"ORGANIZADOR".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los organizadores pueden eliminar actividades");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        ActividadCronograma actividad = cronogramaService.obtenerPorId(id).orElse(null);
        String eventoId = actividad != null ? actividad.getEventoId() : null;
        
        try {
            cronogramaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Actividad eliminada");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar actividad");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        
        return "redirect:/cronograma/evento/" + eventoId;
    }
    
    // Vista pública del cronograma - Accesible para todos los roles
    @GetMapping("/ver/{eventoId}")
    public String verCronograma(@PathVariable String eventoId,
                               @RequestParam(required = false) String tipo,
                               @RequestParam(required = false) String fecha,
                               HttpSession session,
                               Model model) {
        // Sin restricción de rol - todos pueden ver el cronograma
        List<ActividadCronograma> actividades;
        
        if (tipo != null && !tipo.isEmpty()) {
            actividades = cronogramaService.obtenerPorEventoYTipo(
                eventoId, ActividadCronograma.TipoActividad.valueOf(tipo));
        } else if (fecha != null && !fecha.isEmpty()) {
            LocalDate fechaSeleccionada = LocalDate.parse(fecha);
            LocalDateTime inicio = fechaSeleccionada.atStartOfDay();
            LocalDateTime fin = fechaSeleccionada.atTime(23, 59, 59);
            actividades = cronogramaService.obtenerPorRangoFecha(eventoId, inicio, fin);
        } else {
            actividades = cronogramaService.obtenerPorEvento(eventoId);
        }
        
        // Agrupar actividades por fecha
        var actividadesPorFecha = actividades.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                a -> a.getFechaHora().toLocalDate(),
                java.util.TreeMap::new,
                java.util.stream.Collectors.toList()
            ));
        
        String rol = (String) session.getAttribute("rol");
        
        model.addAttribute("evento", eventoService.obtenerPorId(eventoId).orElse(null));
        model.addAttribute("actividades", actividades);
        model.addAttribute("actividadesPorFecha", actividadesPorFecha);
        model.addAttribute("tipos", ActividadCronograma.TipoActividad.values());
        model.addAttribute("tipo", tipo);
        model.addAttribute("fecha", fecha);
        model.addAttribute("eventoId", eventoId);
        model.addAttribute("rol", rol);
        return "visitante/cronograma";
    }
}