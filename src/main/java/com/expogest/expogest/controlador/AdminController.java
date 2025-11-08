package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/panelAdmin")
    public String panelAdmin() {
        return "admin/panelAdmin";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("accion", "Crear");
        return "admin/nuevoUsuario";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            model.addAttribute("accion", "Editar");
            return "admin/nuevoUsuario";
        } else {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            // Validar que el correo no esté duplicado (excepto si es el mismo usuario)
            Optional<Usuario> existente = usuarioRepository.findByCorreo(usuario.getCorreo());
            if (existente.isPresent() && !existente.get().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "El correo ya está registrado");
                return "redirect:/admin/usuarios/nuevo";
            }
            
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario guardado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);
            if (usuario.isPresent()) {
                usuarioRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}