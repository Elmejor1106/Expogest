package com.expogest.expogest.controlador;

import com.expogest.expogest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VisitanteController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/visitante/panelVisitante")
    public String panelVisitante() {
        return "visitante/panelVisitante";
    }

    @GetMapping("/visitante/visitantes")
    public String listarVisitantes(Model model) {
        model.addAttribute("visitantes", usuarioRepository.findAll().stream().filter(u -> u.getRol() == com.expogest.expogest.entidades.Usuario.Rol.VISITANTE).toList());
        return "visitante/visitante";
    }
}