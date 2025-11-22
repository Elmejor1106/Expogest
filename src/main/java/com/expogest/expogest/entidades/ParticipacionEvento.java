package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "participaciones")
public class ParticipacionEvento {
    
    @Id
    private String id;
    private String eventoId;
    private String visitanteId;
    private LocalDateTime fechaInscripcion;
    private EstadoParticipacion estado;
    
    public enum EstadoParticipacion {
        CONFIRMADA,
        CANCELADA
    }
    
    // Constructores
    public ParticipacionEvento() {
        this.fechaInscripcion = LocalDateTime.now();
        this.estado = EstadoParticipacion.CONFIRMADA; // Automática
    }
    
    public ParticipacionEvento(String eventoId, String visitanteId) {
        this();
        this.eventoId = eventoId;
        this.visitanteId = visitanteId;
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getEventoId() {
        return eventoId;
    }
    
    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }
    
    public String getVisitanteId() {
        return visitanteId;
    }
    
    public void setVisitanteId(String visitanteId) {
        this.visitanteId = visitanteId;
    }
    
    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }
    
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }
    
    public EstadoParticipacion getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoParticipacion estado) {
        this.estado = estado;
    }
}
