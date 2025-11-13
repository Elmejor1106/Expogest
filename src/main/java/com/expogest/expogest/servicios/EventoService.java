package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Evento.EstadoEvento;
import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.repository.EventoRepository;
import com.expogest.expogest.repository.StandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private StandRepository standRepository;

    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> obtenerPorId(String id) {
        return eventoRepository.findById(id);
    }

    public Evento guardar(Evento evento) {
        // Validar fechas
        if (evento.getFechaInicio() != null && evento.getFechaFin() != null) {
            if (evento.getFechaInicio().isAfter(evento.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
            }
        }

        // Si es nuevo evento, establecer estado inicial
        if (evento.getId() == null && evento.getEstado() == null) {
            evento.setEstado(EstadoEvento.PLANIFICACION);
        }

        return eventoRepository.save(evento);
    }

    public void eliminar(String id) {
        // Verificar si el evento tiene stands asociados
        Optional<Evento> eventoOpt = eventoRepository.findById(id);
        if (eventoOpt.isPresent() && eventoOpt.get().getStandsAsociados() != null 
            && !eventoOpt.get().getStandsAsociados().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar un evento con stands asociados");
        }
        eventoRepository.deleteById(id);
    }

    public boolean asociarStand(String eventoId, String standId) {
        Optional<Evento> eventoOpt = eventoRepository.findById(eventoId);
        Optional<Stand> standOpt = standRepository.findById(standId);

        if (eventoOpt.isEmpty() || standOpt.isEmpty()) {
            return false;
        }

        Evento evento = eventoOpt.get();
        Stand stand = standOpt.get();

        // Verificar si ya está asociado para evitar duplicados
        if (evento.getStandsAsociados().contains(standId)) {
            throw new IllegalStateException("El stand ya está asociado a este evento");
        }

        // Validar capacidad
        if (!evento.tieneCapacidadDisponible()) {
            throw new IllegalStateException("El evento ha alcanzado su capacidad máxima de stands");
        }

        // Validar que el stand esté disponible
        if (!stand.estaDisponible()) {
            throw new IllegalStateException("El stand no está disponible");
        }

        // Validar que el stand no esté ya asociado a otro evento
        if (stand.getEventoId() != null && !stand.getEventoId().equals(eventoId)) {
            throw new IllegalStateException("El stand ya está asociado a otro evento");
        }

        // Asociar stand al evento
        evento.getStandsAsociados().add(standId);
        stand.setEventoId(eventoId);
        // Mantener como DISPONIBLE para que expositores puedan solicitarlo
        // stand.setEstado(Stand.EstadoStand.DISPONIBLE); // Ya está disponible

        eventoRepository.save(evento);
        standRepository.save(stand);

        return true;
    }

    public boolean desasociarStand(String eventoId, String standId) {
        Optional<Evento> eventoOpt = eventoRepository.findById(eventoId);
        Optional<Stand> standOpt = standRepository.findById(standId);

        if (eventoOpt.isEmpty() || standOpt.isEmpty()) {
            return false;
        }

        Evento evento = eventoOpt.get();
        Stand stand = standOpt.get();

        // Remover asociación
        evento.getStandsAsociados().remove(standId);
        stand.setEventoId(null);
        stand.setExpositorId(null);
        stand.setEstado(Stand.EstadoStand.DISPONIBLE);

        eventoRepository.save(evento);
        standRepository.save(stand);

        return true;
    }

    public List<Stand> obtenerStandsDelEvento(String eventoId) {
        return standRepository.findByEventoId(eventoId);
    }

    public List<Evento> obtenerEventosActivos() {
        return eventoRepository.findByEstado(EstadoEvento.ACTIVO);
    }

    public void actualizarEstadoEvento(String eventoId, EstadoEvento nuevoEstado) {
        Optional<Evento> eventoOpt = eventoRepository.findById(eventoId);
        if (eventoOpt.isPresent()) {
            Evento evento = eventoOpt.get();
            evento.setEstado(nuevoEstado);
            eventoRepository.save(evento);
        }
    }
}
