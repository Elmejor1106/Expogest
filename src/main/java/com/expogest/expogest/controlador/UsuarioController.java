package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        // Validación: NO permitir registro como ADMINISTRADOR
        if (usuario.getRol() == Usuario.Rol.ADMINISTRADOR) {
            model.addAttribute("error", "No está permitido registrarse como Administrador");
            model.addAttribute("usuario", usuario);
            return "registro";
        }
        
        // Validar que el correo no esté duplicado
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            model.addAttribute("error", "El correo ya está registrado");
            model.addAttribute("usuario", usuario);
            return "registro";
        }
        
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUsuario(@RequestParam String correo, @RequestParam String contrasena, Model model) {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario != null && contrasena.equals(usuario.getContrasena())) {
            // Redirección según rol (enum)
            switch (usuario.getRol()) {
                case ADMINISTRADOR:
                    return "redirect:/admin/panelAdmin";
                case ORGANIZADOR:
                    return "redirect:/organizador/panelOrganizador";
                case EXPOSITOR:
                    return "redirect:/expositor/panelExpositor";
                case VISITANTE:
                    return "redirect:/visitante/panelVisitante";
                case EVALUADOR:
                    return "redirect:/evaluador/panelEvaluador";
                default:
                    return "redirect:/";
            }
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";
        }
    }

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

}