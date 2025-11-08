package com.expogest.expogest.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrganizadorController {

    @GetMapping("/organizador/panelOrganizador")
    public String panelOrganizador() {
        return "organizador/panelOrganizador";
    }

    @GetMapping("/organizador/stands")
    public String stands() {
        return "organizador/stands";
    }

    @GetMapping("/organizador/cronograma")
    public String cronograma() {
        return "organizador/cronograma";
    }
}