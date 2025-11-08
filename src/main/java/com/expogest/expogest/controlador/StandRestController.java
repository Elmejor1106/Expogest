package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.repository.StandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stands")
public class StandRestController {

    @Autowired
    private StandRepository standRepository;

    // Listar todos los stands
    @GetMapping
    public ResponseEntity<List<Stand>> listarStands() {
        return ResponseEntity.ok(standRepository.findAll());
    }

    // Obtener stand por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerStand(@PathVariable String id) {
        return standRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nuevo stand (solo organizador)
    @PostMapping
    public ResponseEntity<?> crearStand(@RequestBody Stand stand) {
        // Validar datos requeridos
        if (stand.getNumero() == null || stand.getNumero().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El número del stand es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        if (stand.getUbicacion() == null || stand.getUbicacion().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La ubicación del stand es obligatoria");
            return ResponseEntity.badRequest().body(error);
        }

        Stand nuevoStand = standRepository.save(stand);
        return ResponseEntity.ok(nuevoStand);
    }

    // Editar stand (solo organizador)
    @PutMapping("/{id}")
    public ResponseEntity<?> editarStand(@PathVariable String id, @RequestBody Stand stand) {
        if (!standRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        stand.setId(id);
        Stand standActualizado = standRepository.save(stand);
        return ResponseEntity.ok(standActualizado);
    }

    // Eliminar stand (solo organizador)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarStand(@PathVariable String id) {
        if (!standRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        standRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Stand eliminado correctamente");
        return ResponseEntity.ok(response);
    }

    // Listar stands disponibles
    @GetMapping("/disponibles")
    public ResponseEntity<List<Stand>> listarStandsDisponibles() {
        // Por ahora devuelve todos, después implementar la lógica de disponibilidad
        return ResponseEntity.ok(standRepository.findAll());
    }
}
