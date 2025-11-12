package com.expogest.expogest.controlador;

import com.expogest.expogest.servicios.EventoService;
import com.expogest.expogest.servicios.StandService;
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

    @GetMapping("/organizador/panelOrganizador")
    public String panelOrganizador() {
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
        return "organizador/cronograma";
    }
}