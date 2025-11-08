package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Cronograma;
import com.expogest.expogest.repository.CronogramaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cronogramas")
public class CronogramaRestController {

    @Autowired
    private CronogramaRepository cronogramaRepository;

    // Listar todos los cronogramas
    @GetMapping
    public ResponseEntity<List<Cronograma>> listarCronogramas() {
        return ResponseEntity.ok(cronogramaRepository.findAll());
    }

    // Obtener cronograma por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCronograma(@PathVariable String id) {
        return cronogramaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nueva actividad en el cronograma (solo organizador)
    @PostMapping
    public ResponseEntity<?> crearActividad(@RequestBody Cronograma cronograma) {
        // Validar datos requeridos
        if (cronograma.getActividad() == null || cronograma.getActividad().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La descripción de la actividad es obligatoria");
            return ResponseEntity.badRequest().body(error);
        }

        if (cronograma.getFecha() == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La fecha de la actividad es obligatoria");
            return ResponseEntity.badRequest().body(error);
        }

        if (cronograma.getHora() == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La hora de la actividad es obligatoria");
            return ResponseEntity.badRequest().body(error);
        }

        Cronograma nuevoCronograma = cronogramaRepository.save(cronograma);
        return ResponseEntity.ok(nuevoCronograma);
    }

    // Editar actividad (solo organizador)
    @PutMapping("/{id}")
    public ResponseEntity<?> editarActividad(@PathVariable String id, @RequestBody Cronograma cronograma) {
        if (!cronogramaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        cronograma.setId(id);
        Cronograma cronogramaActualizado = cronogramaRepository.save(cronograma);
        return ResponseEntity.ok(cronogramaActualizado);
    }

    // Eliminar actividad (solo organizador)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarActividad(@PathVariable String id) {
        if (!cronogramaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        cronogramaRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Actividad eliminada correctamente");
        return ResponseEntity.ok(response);
    }

    // Listar actividades por evento
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<Cronograma>> listarPorEvento(@PathVariable String eventoId) {
        List<Cronograma> cronogramas = cronogramaRepository.findAll().stream()
                .filter(c -> eventoId.equals(c.getEventoId()))
                .toList();
        return ResponseEntity.ok(cronogramas);
    }
}
