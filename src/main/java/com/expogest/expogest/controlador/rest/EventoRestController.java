package com.expogest.expogest.controlador.rest;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.servicios.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoRestController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<Evento>> obtenerTodos() {
        return ResponseEntity.ok(eventoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerPorId(@PathVariable String id) {
        return eventoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Evento>> obtenerActivos() {
        return ResponseEntity.ok(eventoService.obtenerEventosActivos());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Evento evento) {
        try {
            Evento guardado = eventoService.guardar(evento);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Evento evento) {
        try {
            evento.setId(id);
            Evento actualizado = eventoService.guardar(evento);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            eventoService.eliminar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Evento eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/stands")
    public ResponseEntity<List<Stand>> obtenerStands(@PathVariable String id) {
        return ResponseEntity.ok(eventoService.obtenerStandsDelEvento(id));
    }

    @PostMapping("/{eventoId}/stands/{standId}")
    public ResponseEntity<?> asociarStand(@PathVariable String eventoId, @PathVariable String standId) {
        try {
            eventoService.asociarStand(eventoId, standId);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Stand asociado exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{eventoId}/stands/{standId}")
    public ResponseEntity<?> desasociarStand(@PathVariable String eventoId, @PathVariable String standId) {
        try {
            eventoService.desasociarStand(eventoId, standId);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Stand desasociado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al desasociar el stand");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
