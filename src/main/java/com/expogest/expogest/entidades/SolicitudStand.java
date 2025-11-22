    package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "solicitudes_stand")
public class SolicitudStand {
    
    @Id
    private String id;
    private String expositorId;
    private String standId;
    private String eventoId;
    private String descripcion;
    private EstadoSolicitud estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;
    private String motivoRechazo;
    private String organizadorId; // Quien aprueba/rechaza

    public enum EstadoSolicitud {
        PENDIENTE,
        APROBADA,
        RECHAZADA,
        CANCELADA
    }

    public SolicitudStand() {
        this.estado = EstadoSolicitud.PENDIENTE;
        this.fechaSolicitud = LocalDateTime.now();
    }

    // Getters y setters
    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }

    public String getExpositorId() { 
        return expositorId; 
    }
    public void setExpositorId(String expositorId) { 
        this.expositorId = expositorId; 
    }

    public String getStandId() { 
        return standId; 
    }
    public void setStandId(String standId) { 
        this.standId = standId; 
    }

    public String getEventoId() { 
        return eventoId; 
    }
    public void setEventoId(String eventoId) { 
        this.eventoId = eventoId; 
    }

    public String getDescripcion() { 
        return descripcion; 
    }
    public void setDescripcion(String descripcion) { 
        this.descripcion = descripcion; 
    }

    public EstadoSolicitud getEstado() { 
        return estado; 
    }
    public void setEstado(EstadoSolicitud estado) { 
        this.estado = estado; 
    }

    public LocalDateTime getFechaSolicitud() { 
        return fechaSolicitud; 
    }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { 
        this.fechaSolicitud = fechaSolicitud; 
    }

    public LocalDateTime getFechaRespuesta() { 
        return fechaRespuesta; 
    }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { 
        this.fechaRespuesta = fechaRespuesta; 
    }

    public String getMotivoRechazo() { 
        return motivoRechazo; 
    }
    public void setMotivoRechazo(String motivoRechazo) { 
        this.motivoRechazo = motivoRechazo; 
    }

    public String getOrganizadorId() { 
        return organizadorId; 
    }
    public void setOrganizadorId(String organizadorId) { 
        this.organizadorId = organizadorId; 
    }

    // Métodos de utilidad
    public boolean estaPendiente() {
        return this.estado == EstadoSolicitud.PENDIENTE;
    }

    public boolean estaAprobada() {
        return this.estado == EstadoSolicitud.APROBADA;
    }

    public boolean estaRechazada() {
        return this.estado == EstadoSolicitud.RECHAZADA;
    }
}