package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.ParticipacionEvento;
import com.expogest.expogest.repositorio.ParticipacionEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ParticipacionEventoService {
    
    @Autowired
    private ParticipacionEventoRepository participacionRepository;
    
    public ParticipacionEvento inscribir(String eventoId, String visitanteId) {
        // Verificar si ya está inscrito
        Optional<ParticipacionEvento> existente = participacionRepository
            .findByEventoIdAndVisitanteId(eventoId, visitanteId);
        
        if (existente.isPresent()) {
            throw new IllegalStateException("Ya está inscrito en este evento");
        }
        
        ParticipacionEvento participacion = new ParticipacionEvento(eventoId, visitanteId);
        return participacionRepository.save(participacion);
    }
    
    public void cancelarInscripcion(String participacionId) {
        ParticipacionEvento participacion = participacionRepository.findById(participacionId)
            .orElseThrow(() -> new IllegalArgumentException("Participación no encontrada"));
        
        participacion.setEstado(ParticipacionEvento.EstadoParticipacion.CANCELADA);
        participacionRepository.save(participacion);
    }
    
    public List<ParticipacionEvento> obtenerPorEvento(String eventoId) {
        return participacionRepository.findByEventoId(eventoId);
    }
    
    public List<ParticipacionEvento> obtenerPorVisitante(String visitanteId) {
        return participacionRepository.findByVisitanteId(visitanteId);
    }
    
    public boolean estaInscrito(String eventoId, String visitanteId) {
        Optional<ParticipacionEvento> participacion = participacionRepository
            .findByEventoIdAndVisitanteId(eventoId, visitanteId);
        return participacion.isPresent() && 
               participacion.get().getEstado() == ParticipacionEvento.EstadoParticipacion.CONFIRMADA;
    }
    
    public long contarParticipantes(String eventoId) {
        return participacionRepository.countByEventoIdAndEstado(
            eventoId, ParticipacionEvento.EstadoParticipacion.CONFIRMADA);
    }
}
