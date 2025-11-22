package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.repository.EventoRepository;
import com.expogest.expogest.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class VisitanteController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping("/visitante/panelVisitante")
    public String panelVisitante() {
        return "visitante/panelVisitante";
    }

    @GetMapping("/visitante/visitantes")
    public String listarVisitantes(Model model) {
        model.addAttribute("visitantes", usuarioRepository.findAll().stream().filter(u -> u.getRol() == com.expogest.expogest.entidades.Usuario.Rol.VISITANTE).toList());
        return "visitante/visitante";
    }
    
    @GetMapping("/visitante/inscripcion")
    public String formularioInscripcion(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String rol = (String) session.getAttribute("rol");
        
        if (!"VISITANTE".equals(rol)) {
            redirectAttributes.addFlashAttribute("mensaje", "Solo los visitantes pueden inscribirse a eventos");
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/";
        }
        
        List<Evento> eventos = eventoRepository.findByEstado(Evento.EstadoEvento.ACTIVO);
        model.addAttribute("eventos", eventos);
        
        return "visitante/inscripcion";
    }
}