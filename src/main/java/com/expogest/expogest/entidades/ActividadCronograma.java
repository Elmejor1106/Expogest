package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "actividades_cronograma")
public class ActividadCronograma {
    
    @Id
    private String id;
    private String eventoId;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private String ubicacion;
    private String speakerId; // ID del expositor (opcional)
    private TipoActividad tipo;
    private Integer capacidadMaxima;
    private Integer inscritos;
    
    public enum TipoActividad {
        CONFERENCIA,
        TALLER,
        NETWORKING,
        PRESENTACION,
        PANEL,
        PANEL_DISCUSION,
        OTRO
    }
    
    // Constructores
    public ActividadCronograma() {
        this.inscritos = 0;
    }
    
    // Métodos útiles
    public boolean tieneCapacidadDisponible() {
        if (capacidadMaxima == null) {
            return true; // Capacidad ilimitada
        }
        return inscritos < capacidadMaxima;
    }
    
    public void incrementarInscritos() {
        this.inscritos++;
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
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }
    
    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    public String getSpeakerId() {
        return speakerId;
    }
    
    public void setSpeakerId(String speakerId) {
        this.speakerId = speakerId;
    }
    
    public TipoActividad getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoActividad tipo) {
        this.tipo = tipo;
    }
    
    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }
    
    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
    
    public Integer getInscritos() {
        return inscritos;
    }
    
    public void setInscritos(Integer inscritos) {
        this.inscritos = inscritos;
    }
}
