package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
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
    public String loginUsuario(@RequestParam String correo, @RequestParam String contrasena, 
                              HttpSession session, Model model) {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario != null && contrasena.equals(usuario.getContrasena())) {
            // Guardar usuario en sesión
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioRol", usuario.getRol().toString());
            session.setAttribute("rol", usuario.getRol().toString()); // Para validaciones
            
            // Redirección según rol (enum)
            Usuario.Rol rol = usuario.getRol();
            if (rol == Usuario.Rol.ADMINISTRADOR) {
                return "redirect:/admin/panelAdmin";
            } else if (rol == Usuario.Rol.ORGANIZADOR) {
                return "redirect:/organizador/panelOrganizador";
            } else if (rol == Usuario.Rol.EXPOSITOR) {
                return "redirect:/expositor/panelExpositor";
            } else if (rol == Usuario.Rol.VISITANTE) {
                return "redirect:/visitante/panelVisitante";
            } else if (rol == Usuario.Rol.EVALUADOR) {
                return "redirect:/evaluador/panel";
            } else {
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidar la sesión
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }

    @GetMapping("/en-construccion")
    public String enConstruccion() {
        return "en-construccion";
    }

}