package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioCrudRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Registro de usuario público
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        // Validación: NO permitir registro como ADMINISTRADOR
        if (usuario.getRol() == Usuario.Rol.ADMINISTRADOR) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No está permitido registrarse como Administrador");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Validar que el correo sea único
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El correo ya está registrado");
            return ResponseEntity.badRequest().body(error);
        }

        // Validar que el rol sea válido antes de guardar
        if (usuario.getRol() == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El rol es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        Usuario nuevoUsuario = usuarioRepository.save(usuario);
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Usuario registrado correctamente");
        response.put("usuario", nuevoUsuario);
        return ResponseEntity.ok(response);
    }

    // Login de usuario
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String correo = loginData.get("correo");
        String contrasena = loginData.get("contrasena");

        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        
        if (usuario == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Usuario no encontrado");
            return ResponseEntity.status(401).body(error);
        }

        if (!usuario.getContrasena().equals(contrasena)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Contraseña incorrecta");
            return ResponseEntity.status(401).body(error);
        }

        // Login exitoso
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Login exitoso");
        response.put("usuario", usuario);
        response.put("rol", usuario.getRol());
        return ResponseEntity.ok(response);
    }

    // Obtener perfil de usuario
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable String id) {
        return usuarioRepository.findById(id)
                .map(usuario -> ResponseEntity.ok(usuario))
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar perfil de usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable String id, @RequestBody Usuario usuario) {
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
}
