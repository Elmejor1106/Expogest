package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.ActividadCronograma;
import com.expogest.expogest.repositorio.ActividadCronogramaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActividadCronogramaService {
    
    @Autowired
    private ActividadCronogramaRepository actividadRepository;
    
    public ActividadCronograma guardar(ActividadCronograma actividad) {
        // Validar que la fecha no sea pasada
        if (actividad.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la actividad no puede ser en el pasado");
        }
        
        return actividadRepository.save(actividad);
    }
    
    public Optional<ActividadCronograma> obtenerPorId(String id) {
        return actividadRepository.findById(id);
    }
    
    public List<ActividadCronograma> obtenerPorEvento(String eventoId) {
        return actividadRepository.findByEventoIdOrderByFechaHoraAsc(eventoId);
    }
    
    public List<ActividadCronograma> obtenerPorEventoYTipo(String eventoId, ActividadCronograma.TipoActividad tipo) {
        return actividadRepository.findByEventoIdAndTipo(eventoId, tipo);
    }
    
    public List<ActividadCronograma> obtenerPorRangoFecha(String eventoId, LocalDateTime inicio, LocalDateTime fin) {
        return actividadRepository.findByEventoIdAndFechaHoraBetween(eventoId, inicio, fin);
    }
    
    public List<ActividadCronograma> obtenerPorSpeaker(String speakerId) {
        return actividadRepository.findBySpeakerId(speakerId);
    }
    
    public List<ActividadCronograma> obtenerTodas() {
        return actividadRepository.findAll();
    }
    
    public void eliminar(String id) {
        actividadRepository.deleteById(id);
    }
    
    public ActividadCronograma inscribirParticipante(String actividadId) {
        ActividadCronograma actividad = obtenerPorId(actividadId)
            .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
        
        if (!actividad.tieneCapacidadDisponible()) {
            throw new IllegalStateException("La actividad ha alcanzado su capacidad máxima");
        }
        
        actividad.incrementarInscritos();
        return actividadRepository.save(actividad);
    }
}
