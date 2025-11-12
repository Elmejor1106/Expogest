package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.SolicitudStand;
import com.expogest.expogest.entidades.SolicitudStand.EstadoSolicitud;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudStandRepository extends MongoRepository<SolicitudStand, String> {
    
    // Buscar solicitudes por expositor
    List<SolicitudStand> findByExpositorId(String expositorId);
    
    // Buscar solicitud específica de un expositor para un evento
    Optional<SolicitudStand> findByExpositorIdAndEventoId(String expositorId, String eventoId);
    
    // Buscar solicitudes por estado
    List<SolicitudStand> findByEstado(EstadoSolicitud estado);
    
    // Buscar solicitudes por evento
    List<SolicitudStand> findByEventoId(String eventoId);
    
    // Buscar solicitudes pendientes de un evento
    List<SolicitudStand> findByEventoIdAndEstado(String eventoId, EstadoSolicitud estado);
    
    // Buscar solicitudes por stand
    List<SolicitudStand> findByStandId(String standId);
    
    // Contar solicitudes de un expositor para un evento
    long countByExpositorIdAndEventoId(String expositorId, String eventoId);
}