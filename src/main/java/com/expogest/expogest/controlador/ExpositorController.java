package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.entidades.SolicitudStand.EstadoSolicitud;
import com.expogest.expogest.servicios.EventoService;
import com.expogest.expogest.servicios.SolicitudStandService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExpositorController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private SolicitudStandService solicitudService;

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
}