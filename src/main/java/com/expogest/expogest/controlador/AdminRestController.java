package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar todos los usuarios (solo admin)
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // Crear nuevo usuario (solo admin)
    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        // Validar que el correo sea único
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El correo ya está registrado");
            return ResponseEntity.badRequest().body(error);
        }

        // Validar que el rol sea válido
        if (usuario.getRol() == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El rol es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // Obtener usuario por ID (solo admin)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable String id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Editar usuario (solo admin)
    @PutMapping("/{id}")
    public ResponseEntity<?> editarUsuario(@PathVariable String id, @RequestBody Usuario usuario) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Validar que el correo sea único (excepto para el mismo usuario)
        Usuario usuarioExistente = usuarioRepository.findByCorreo(usuario.getCorreo()).orElse(null);
        if (usuarioExistente != null && !usuarioExistente.getId().equals(id)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El correo ya está registrado por otro usuario");
            return ResponseEntity.badRequest().body(error);
        }

        usuario.setId(id);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // Eliminar usuario (solo admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    // Filtrar usuarios por rol (solo admin)
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Usuario>> listarUsuariosPorRol(@PathVariable String rol) {
        try {
            Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol.toUpperCase());
            List<Usuario> usuarios = usuarioRepository.findAll().stream()
                    .filter(u -> u.getRol() == rolEnum)
                    .toList();
            return ResponseEntity.ok(usuarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
