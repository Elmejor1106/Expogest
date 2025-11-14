package com.expogest.expogest.controlador;

import com.expogest.expogest.servicios.EventoService;
import com.expogest.expogest.servicios.StandService;
import com.expogest.expogest.servicios.SolicitudStandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrganizadorController {

    @Autowired
    private StandService standService;

    @Autowired
    private EventoService eventoService;
    
    @Autowired
    private SolicitudStandService solicitudService;

    @GetMapping("/organizador/panelOrganizador")
    public String panelOrganizador(Model model) {
        // Obtener estadísticas
        model.addAttribute("eventosStats", eventoService.obtenerEstadisticas());
        model.addAttribute("solicitudesStats", solicitudService.obtenerEstadisticas());
        model.addAttribute("solicitudesPendientes", solicitudService.contarPendientes());
        
        // Stands disponibles vs ocupados
        long standsDisponibles = standService.obtenerDisponibles().size();
        long standsTotal = standService.obtenerTodos().size();
        model.addAttribute("standsDisponibles", standsDisponibles);
        model.addAttribute("standsOcupados", standsTotal - standsDisponibles);
        model.addAttribute("standsTotal", standsTotal);
        
        // Próximos eventos
        model.addAttribute("eventosProximos", eventoService.obtenerEventosProximos());
        
        return "organizador/panelOrganizador";
    }

    @GetMapping("/organizador/stands")
    public String stands(Model model) {
        model.addAttribute("stands", standService.obtenerTodos());
        model.addAttribute("eventos", eventoService.obtenerTodos());
        return "organizador/stands";
    }

    @GetMapping("/organizador/cronograma")
    public String cronograma() {
        return "redirect:/en-construccion";
    }
}