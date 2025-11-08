package com.expogest.expogest.controlador;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.entidades.Cronograma;
import com.expogest.expogest.repository.EventoRepository;
import com.expogest.expogest.repository.StandRepository;
import com.expogest.expogest.repository.CronogramaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizador")
public class OrganizadorRestController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private StandRepository standRepository;

    @Autowired
    private CronogramaRepository cronogramaRepository;

    // Dashboard del organizador
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalEventos", eventoRepository.count());
        data.put("totalStands", standRepository.count());
        data.put("totalActividades", cronogramaRepository.count());
        return ResponseEntity.ok(data);
    }

    // Gestionar eventos del organizador
    @GetMapping("/eventos")
    public ResponseEntity<List<Evento>> misEventos() {
        return ResponseEntity.ok(eventoRepository.findAll());
    }

    // Gestionar stands del organizador
    @GetMapping("/stands")
    public ResponseEntity<List<Stand>> misStands() {
        return ResponseEntity.ok(standRepository.findAll());
    }

    // Gestionar cronogramas del organizador
    @GetMapping("/cronogramas")
    public ResponseEntity<List<Cronograma>> misCronogramas() {
        return ResponseEntity.ok(cronogramaRepository.findAll());
    }
}
