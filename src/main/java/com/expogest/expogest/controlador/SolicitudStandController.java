package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.SolicitudStand;
import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.servicios.SolicitudStandService;
import com.expogest.expogest.servicios.EventoService;
import com.expogest.expogest.servicios.StandService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudStandController {

    @Autowired
    private SolicitudStandService solicitudService;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private StandService standService;

    /**
     * Listar todas las solicitudes (para organizador/admin)
     */
    @GetMapping
    public String listarSolicitudes(Model model) {
        model.addAttribute("solicitudes", solicitudService.obtenerTodas());
        return "solicitud/lista";
    }

    /**
     * Listar solicitudes pendientes (para organizador)
     */
    @GetMapping("/pendientes")
    public String listarPendientes(Model model) {
        model.addAttribute("solicitudes", solicitudService.obtenerPendientes());
        return "solicitud/pendientes";
    }

    /**
     * Listar solicitudes por evento (para organizador)
     */
    @GetMapping("/evento/{eventoId}")
    public String listarPorEvento(@PathVariable String eventoId, Model model) {
        model.addAttribute("evento", eventoService.obtenerPorId(eventoId).orElse(null));
        model.addAttribute("solicitudes", solicitudService.obtenerPorEvento(eventoId));
        model.addAttribute("pendientes", solicitudService.obtenerPendientesPorEvento(eventoId));
        return "solicitud/porEvento";
    }

    /**
     * Formulario para nueva solicitud (expositor)
     */
    @GetMapping("/nueva/{eventoId}")
    public String nuevaSolicitud(@PathVariable String eventoId, Model model, RedirectAttributes redirectAttributes) {
        try {
            SolicitudStand solicitud = new SolicitudStand();
            solicitud.setEventoId(eventoId);
            
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("evento", eventoService.obtenerPorId(eventoId).orElse(null));
            model.addAttribute("stands", eventoService.obtenerStandsDelEvento(eventoId));
            model.addAttribute("standsDisponibles", standService.obtenerPorEvento(eventoId)
                .stream()
                .filter(s -> s.estaDisponible() || s.getEstado() == com.expogest.expogest.entidades.Stand.EstadoStand.RESERVADO)
                .toList());
            
            return "solicitud/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al cargar el formulario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/eventos";
        }
    }

    /**
     * Guardar solicitud (expositor)
     */
    @PostMapping("/guardar")
    public String guardarSolicitud(@ModelAttribute SolicitudStand solicitud, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario == null) {
                return "redirect:/login";
            }
            solicitud.setExpositorId(usuario.getId());
            
            solicitudService.crearSolicitud(solicitud);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud enviada exitosamente. Estará pendiente de aprobación.");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/solicitudes/mis-solicitudes";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/solicitudes/nueva/" + solicitud.getEventoId();
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "warning");
            return "redirect:/solicitudes/nueva/" + solicitud.getEventoId();
        }
    }

    /**
     * Mis solicitudes (expositor)
     */
    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("solicitudes", solicitudService.obtenerPorExpositor(usuario.getId()));
        return "solicitud/misSolicitudes";
    }

    /**
     * Aprobar solicitud (organizador)
     */
    @PostMapping("/{id}/aprobar")
    public String aprobarSolicitud(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario == null) {
                return "redirect:/login";
            }
            String organizadorId = usuario.getId();
            
            solicitudService.aprobarSolicitud(id, organizadorId);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud aprobada exitosamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/solicitudes/pendientes";
    }

    /**
     * Rechazar solicitud (organizador)
     */
    @PostMapping("/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable String id, 
                                   @RequestParam String motivo,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario == null) {
                return "redirect:/login";
            }
            String organizadorId = usuario.getId();
            
            solicitudService.rechazarSolicitud(id, organizadorId, motivo);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud rechazada");
            redirectAttributes.addFlashAttribute("tipo", "info");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/solicitudes/pendientes";
    }

    /**
     * Cancelar solicitud (expositor)
     */
    @PostMapping("/{id}/cancelar")
    public String cancelarSolicitud(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario == null) {
                return "redirect:/login";
            }
            String expositorId = usuario.getId();
            
            solicitudService.cancelarSolicitud(id, expositorId);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud cancelada");
            redirectAttributes.addFlashAttribute("tipo", "info");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/solicitudes/mis-solicitudes";
    }

    /**
     * Eliminar solicitud
     */
    @GetMapping("/{id}/eliminar")
    public String eliminarSolicitud(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            solicitudService.eliminarSolicitud(id);
            redirectAttributes.addFlashAttribute("mensaje", "Solicitud eliminada");
            redirectAttributes.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/solicitudes";
    }
}