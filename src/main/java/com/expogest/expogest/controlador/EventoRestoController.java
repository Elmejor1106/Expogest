package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoRestoController {

    @Autowired
    private EventoRepository eventoRepository;

    // Listar todos los eventos
    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(eventoRepository.findAll());
    }

    // Obtener evento por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEvento(@PathVariable String id) {
        return eventoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear nuevo evento (solo admin u organizador)
    @PostMapping
    public ResponseEntity<?> crearEvento(@RequestBody Evento evento) {
        // Validar que los datos requeridos estén presentes
        if (evento.getNombre() == null || evento.getNombre().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El nombre del evento es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        if (evento.getFechas() == null || evento.getFechas().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Las fechas del evento son obligatorias");
            return ResponseEntity.badRequest().body(error);
        }

        if (evento.getLugar() == null || evento.getLugar().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El lugar del evento es obligatorio");
            return ResponseEntity.badRequest().body(error);
        }

        Evento nuevoEvento = eventoRepository.save(evento);
        return ResponseEntity.ok(nuevoEvento);
    }

    // Editar evento (solo admin u organizador)
    @PutMapping("/{id}")
    public ResponseEntity<?> editarEvento(@PathVariable String id, @RequestBody Evento evento) {
        if (!eventoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        evento.setId(id);
        Evento eventoActualizado = eventoRepository.save(evento);
        return ResponseEntity.ok(eventoActualizado);
    }

    // Eliminar evento (solo admin u organizador)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEvento(@PathVariable String id) {
        if (!eventoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Aquí deberías validar que no haya expositores activos
        eventoRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Evento eliminado correctamente");
        return ResponseEntity.ok(response);
    }
}
