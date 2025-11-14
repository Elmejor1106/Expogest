package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Evento.EstadoEvento;
import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.entidades.SolicitudStand.EstadoSolicitud;
import com.expogest.expogest.repository.EventoRepository;
import com.expogest.expogest.repository.StandRepository;
import com.expogest.expogest.repository.SolicitudStandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private StandRepository standRepository;
    
    @Autowired
    private SolicitudStandRepository solicitudRepository;

    public List<Evento> obtenerTodos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> obtenerPorId(String id) {
        return eventoRepository.findById(id);
    }

    public Evento guardar(Evento evento) {
        // Validar coordenadas geográficas (obligatorias)
        if (evento.getLatitud() == null || evento.getLongitud() == null) {
            throw new IllegalArgumentException("Las coordenadas de ubicación del evento son obligatorias. Por favor, seleccione la ubicación en el mapa");
        }
        
        // Validar rango de latitud (-90 a 90)
        if (evento.getLatitud() < -90 || evento.getLatitud() > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90 grados");
        }
        
        // Validar rango de longitud (-180 a 180)
        if (evento.getLongitud() < -180 || evento.getLongitud() > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180 grados");
        }
        
        // Validar fechas
        if (evento.getFechaInicio() != null && evento.getFechaFin() != null) {
            // Validar que fecha fin sea mayor que fecha inicio
            if (evento.getFechaInicio().isAfter(evento.getFechaFin())) {
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
            }
            
            // Validar que no se creen eventos con fechas pasadas (solo para nuevos eventos)
            if (evento.getId() == null) {
                LocalDate hoy = LocalDate.now();
                if (evento.getFechaInicio().isBefore(hoy)) {
                    throw new IllegalArgumentException("No se pueden crear eventos con fecha de inicio en el pasado");
                }
            }
        }
        
        // Validar capacidad mínima de stands
        if (evento.getCapacidadMaximaStands() != null && evento.getCapacidadMaximaStands() < 1) {
            throw new IllegalArgumentException("La capacidad mínima de stands debe ser al menos 1");
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
        
        // Verificar si el evento tiene solicitudes aprobadas
        long solicitudesAprobadas = solicitudRepository.countByEventoIdAndEstado(id, 
            com.expogest.expogest.entidades.SolicitudStand.EstadoSolicitud.APROBADA);
        if (solicitudesAprobadas > 0) {
            throw new IllegalStateException("No se puede eliminar un evento con solicitudes aprobadas. Tiene " 
                + solicitudesAprobadas + " solicitud(es) aprobada(s)");
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
    
    /**
     * Buscar eventos por nombre
     */
    public List<Evento> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return eventoRepository.findAll();
        }
        return eventoRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }
    
    /**
     * Buscar eventos por descripción
     */
    public List<Evento> buscarPorDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return eventoRepository.findAll();
        }
        return eventoRepository.findByDescripcionContainingIgnoreCase(descripcion.trim());
    }
    
    /**
     * Buscar eventos próximos activos
     */
    public List<Evento> obtenerEventosProximos() {
        return eventoRepository.findByEstadoAndFechaInicioAfterOrderByFechaInicioAsc(
            EstadoEvento.ACTIVO, 
            LocalDate.now()
        );
    }
    
    /**
     * Obtener estadísticas de eventos
     */
    public Map<String, Long> obtenerEstadisticas() {
        List<Evento> todosLosEventos = eventoRepository.findAll();
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("total", (long) todosLosEventos.size());
        stats.put("activos", todosLosEventos.stream()
            .filter(e -> e.getEstado() == EstadoEvento.ACTIVO).count());
        stats.put("planificacion", todosLosEventos.stream()
            .filter(e -> e.getEstado() == EstadoEvento.PLANIFICACION).count());
        stats.put("finalizados", todosLosEventos.stream()
            .filter(e -> e.getEstado() == EstadoEvento.FINALIZADO).count());
        stats.put("cancelados", todosLosEventos.stream()
            .filter(e -> e.getEstado() == EstadoEvento.CANCELADO).count());
        
        return stats;
    }
}
