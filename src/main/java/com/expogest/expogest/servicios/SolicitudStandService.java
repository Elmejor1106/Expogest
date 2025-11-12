package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Evento.EstadoEvento;
import com.expogest.expogest.entidades.SolicitudStand;
import com.expogest.expogest.entidades.SolicitudStand.EstadoSolicitud;
import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.repository.EventoRepository;
import com.expogest.expogest.repository.SolicitudStandRepository;
import com.expogest.expogest.repository.StandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudStandService {

    @Autowired
    private SolicitudStandRepository solicitudRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private StandRepository standRepository;

    @Autowired
    private StandService standService;

    /**
     * Obtener todas las solicitudes
     */
    public List<SolicitudStand> obtenerTodas() {
        return solicitudRepository.findAll();
    }

    /**
     * Obtener solicitud por ID
     */
    public Optional<SolicitudStand> obtenerPorId(String id) {
        return solicitudRepository.findById(id);
    }

    /**
     * Obtener solicitudes de un expositor
     */
    public List<SolicitudStand> obtenerPorExpositor(String expositorId) {
        return solicitudRepository.findByExpositorId(expositorId);
    }

    /**
     * Obtener solicitudes de un evento
     */
    public List<SolicitudStand> obtenerPorEvento(String eventoId) {
        return solicitudRepository.findByEventoId(eventoId);
    }

    /**
     * Obtener solicitudes pendientes
     */
    public List<SolicitudStand> obtenerPendientes() {
        return solicitudRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    /**
     * Obtener solicitudes pendientes de un evento
     */
    public List<SolicitudStand> obtenerPendientesPorEvento(String eventoId) {
        return solicitudRepository.findByEventoIdAndEstado(eventoId, EstadoSolicitud.PENDIENTE);
    }

    /**
     * Crear nueva solicitud con validaciones completas
     */
    public SolicitudStand crearSolicitud(SolicitudStand solicitud) {
        
        // Validar que el evento existe
        Optional<Evento> eventoOpt = eventoRepository.findById(solicitud.getEventoId());
        if (eventoOpt.isEmpty()) {
            throw new IllegalArgumentException("El evento no existe");
        }
        
        Evento evento = eventoOpt.get();
        
        // Validar que el evento está activo
        if (evento.getEstado() != EstadoEvento.ACTIVO) {
            throw new IllegalStateException("Solo se pueden solicitar stands para eventos activos");
        }

        // Validar que el stand existe
        Optional<Stand> standOpt = standRepository.findById(solicitud.getStandId());
        if (standOpt.isEmpty()) {
            throw new IllegalArgumentException("El stand no existe");
        }

        Stand stand = standOpt.get();

        // Validar que el stand está disponible
        if (!stand.estaDisponible()) {
            throw new IllegalStateException("El stand no está disponible");
        }

        // Validar que el stand pertenece al evento
        if (stand.getEventoId() == null || !stand.getEventoId().equals(solicitud.getEventoId())) {
            throw new IllegalStateException("El stand no está asociado a este evento");
        }

        // Validar que el expositor no tenga ya una solicitud para este evento
        Optional<SolicitudStand> solicitudExistente = 
            solicitudRepository.findByExpositorIdAndEventoId(
                solicitud.getExpositorId(), 
                solicitud.getEventoId()
            );

        if (solicitudExistente.isPresent()) {
            SolicitudStand sol = solicitudExistente.get();
            if (sol.getEstado() == EstadoSolicitud.PENDIENTE) {
                throw new IllegalStateException("Ya tiene una solicitud pendiente para este evento");
            }
            if (sol.getEstado() == EstadoSolicitud.APROBADA) {
                throw new IllegalStateException("Ya tiene una solicitud aprobada para este evento");
            }
        }

        // Establecer valores por defecto
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }

    /**
     * Aprobar solicitud
     */
    public SolicitudStand aprobarSolicitud(String solicitudId, String organizadorId) {
        Optional<SolicitudStand> solicitudOpt = solicitudRepository.findById(solicitudId);
        if (solicitudOpt.isEmpty()) {
            throw new IllegalArgumentException("La solicitud no existe");
        }

        SolicitudStand solicitud = solicitudOpt.get();

        // Validar que la solicitud está pendiente
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aprobar solicitudes pendientes");
        }

        // Validar que el stand sigue disponible
        Optional<Stand> standOpt = standRepository.findById(solicitud.getStandId());
        if (standOpt.isEmpty()) {
            throw new IllegalArgumentException("El stand no existe");
        }

        Stand stand = standOpt.get();
        if (!stand.estaDisponible() && stand.getEstado() != Stand.EstadoStand.RESERVADO) {
            throw new IllegalStateException("El stand ya no está disponible");
        }

        // Asignar el stand al expositor
        standService.asignarExpositor(solicitud.getStandId(), solicitud.getExpositorId());

        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitud.APROBADA);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitud.setOrganizadorId(organizadorId);

        return solicitudRepository.save(solicitud);
    }

    /**
     * Rechazar solicitud
     */
    public SolicitudStand rechazarSolicitud(String solicitudId, String organizadorId, String motivo) {
        Optional<SolicitudStand> solicitudOpt = solicitudRepository.findById(solicitudId);
        if (solicitudOpt.isEmpty()) {
            throw new IllegalArgumentException("La solicitud no existe");
        }

        SolicitudStand solicitud = solicitudOpt.get();

        // Validar que la solicitud está pendiente
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar solicitudes pendientes");
        }

        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitud.setOrganizadorId(organizadorId);
        solicitud.setMotivoRechazo(motivo);

        return solicitudRepository.save(solicitud);
    }

    /**
     * Cancelar solicitud (por el expositor)
     */
    public SolicitudStand cancelarSolicitud(String solicitudId, String expositorId) {
        Optional<SolicitudStand> solicitudOpt = solicitudRepository.findById(solicitudId);
        if (solicitudOpt.isEmpty()) {
            throw new IllegalArgumentException("La solicitud no existe");
        }

        SolicitudStand solicitud = solicitudOpt.get();

        // Validar que es el expositor de la solicitud
        if (!solicitud.getExpositorId().equals(expositorId)) {
            throw new IllegalArgumentException("No tiene permisos para cancelar esta solicitud");
        }

        // Validar que la solicitud está pendiente
        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden cancelar solicitudes pendientes");
        }

        // Actualizar solicitud
        solicitud.setEstado(EstadoSolicitud.CANCELADA);
        solicitud.setFechaRespuesta(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }

    /**
     * Eliminar solicitud
     */
    public void eliminarSolicitud(String solicitudId) {
        Optional<SolicitudStand> solicitudOpt = solicitudRepository.findById(solicitudId);
        if (solicitudOpt.isPresent()) {
            SolicitudStand solicitud = solicitudOpt.get();
            
            // Solo se pueden eliminar solicitudes canceladas o rechazadas
            if (solicitud.getEstado() == EstadoSolicitud.APROBADA) {
                throw new IllegalStateException("No se puede eliminar una solicitud aprobada");
            }
            
            if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
                throw new IllegalStateException("Debe cancelar la solicitud antes de eliminarla");
            }
        }
        
        solicitudRepository.deleteById(solicitudId);
    }

    /**
     * Verificar si un expositor puede solicitar un stand en un evento
     */
    public boolean puedesolicitarStand(String expositorId, String eventoId) {
        Optional<SolicitudStand> solicitudExistente = 
            solicitudRepository.findByExpositorIdAndEventoId(expositorId, eventoId);
        
        if (solicitudExistente.isPresent()) {
            EstadoSolicitud estado = solicitudExistente.get().getEstado();
            return estado == EstadoSolicitud.RECHAZADA || estado == EstadoSolicitud.CANCELADA;
        }
        
        return true;
    }

    /**
     * Obtener solicitudes por stand
     */
    public List<SolicitudStand> obtenerPorStand(String standId) {
        return solicitudRepository.findByStandId(standId);
    }
}
