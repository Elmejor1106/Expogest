package com.expogest.expogest.controlador;

import com.expogest.expogest.servicios.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExpositorController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/expositor/panelExpositor")
    public String panelExpositor() {
        return "expositor/panelExpositor";
    }

    @GetMapping("/expositor/solicitudStand")
    public String solicitudStand() {
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