package com.expogest.expogest.controlador.rest;

import com.expogest.expogest.entidades.SolicitudStand;
import com.expogest.expogest.servicios.SolicitudStandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudStandRestController {

    @Autowired
    private SolicitudStandService solicitudService;

    @GetMapping
    public ResponseEntity<List<SolicitudStand>> obtenerTodas() {
        return ResponseEntity.ok(solicitudService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudStand> obtenerPorId(@PathVariable String id) {
        return solicitudService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/expositor/{expositorId}")
    public ResponseEntity<List<SolicitudStand>> obtenerPorExpositor(@PathVariable String expositorId) {
        return ResponseEntity.ok(solicitudService.obtenerPorExpositor(expositorId));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<SolicitudStand>> obtenerPorEvento(@PathVariable String eventoId) {
        return ResponseEntity.ok(solicitudService.obtenerPorEvento(eventoId));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudStand>> obtenerPendientes() {
        return ResponseEntity.ok(solicitudService.obtenerPendientes());
    }

    @GetMapping("/evento/{eventoId}/pendientes")
    public ResponseEntity<List<SolicitudStand>> obtenerPendientesPorEvento(@PathVariable String eventoId) {
        return ResponseEntity.ok(solicitudService.obtenerPendientesPorEvento(eventoId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody SolicitudStand solicitud) {
        try {
            SolicitudStand nuevaSolicitud = solicitudService.crearSolicitud(solicitud);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSolicitud);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable String id, @RequestParam String organizadorId) {
        try {
            SolicitudStand solicitud = solicitudService.aprobarSolicitud(id, organizadorId);
            return ResponseEntity.ok(solicitud);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable String id, 
                                     @RequestParam String organizadorId,
                                     @RequestParam String motivo) {
        try {
            SolicitudStand solicitud = solicitudService.rechazarSolicitud(id, organizadorId, motivo);
            return ResponseEntity.ok(solicitud);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable String id, @RequestParam String expositorId) {
        try {
            SolicitudStand solicitud = solicitudService.cancelarSolicitud(id, expositorId);
            return ResponseEntity.ok(solicitud);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            solicitudService.eliminarSolicitud(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Solicitud eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/stand/{standId}")
    public ResponseEntity<List<SolicitudStand>> obtenerPorStand(@PathVariable String standId) {
        return ResponseEntity.ok(solicitudService.obtenerPorStand(standId));
    }

    @GetMapping("/puede-solicitar")
    public ResponseEntity<Map<String, Boolean>> puedeSolicitar(@RequestParam String expositorId, 
                                                                 @RequestParam String eventoId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("puede", solicitudService.puedesolicitarStand(expositorId, eventoId));
        return ResponseEntity.ok(response);
    }
}
