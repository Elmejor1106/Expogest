package com.expogest.expogest.controlador.rest;

import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.servicios.StandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stands")
public class StandRestController {

    @Autowired
    private StandService standService;

    @GetMapping
    public ResponseEntity<List<Stand>> obtenerTodos() {
        return ResponseEntity.ok(standService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stand> obtenerPorId(@PathVariable String id) {
        return standService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<Stand> obtenerPorNumero(@PathVariable String numero) {
        return standService.obtenerPorNumero(numero)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Stand>> obtenerDisponibles() {
        return ResponseEntity.ok(standService.obtenerDisponibles());
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<Stand>> obtenerPorEvento(@PathVariable String eventoId) {
        return ResponseEntity.ok(standService.obtenerPorEvento(eventoId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Stand stand) {
        try {
            Stand guardado = standService.guardar(stand);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Stand stand) {
        try {
            stand.setId(id);
            Stand actualizado = standService.guardar(stand);
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
            standService.eliminar(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Stand eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{standId}/asignar-expositor/{expositorId}")
    public ResponseEntity<?> asignarExpositor(@PathVariable String standId, @PathVariable String expositorId) {
        try {
            standService.asignarExpositor(standId, expositorId);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Expositor asignado exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{standId}/liberar")
    public ResponseEntity<?> liberar(@PathVariable String standId) {
        standService.liberarStand(standId);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Stand liberado exitosamente");
        return ResponseEntity.ok(response);
    }
}
