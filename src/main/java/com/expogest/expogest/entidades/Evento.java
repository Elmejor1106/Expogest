package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "eventos")
public class Evento {
    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String lugar;
    private EstadoEvento estado;
    private Integer capacidadMaximaStands;
    private List<String> standsAsociados; // IDs de stands asociados
    
    // Coordenadas geográficas para ubicación del evento en mapa
    private Double latitud;
    private Double longitud;

    public enum EstadoEvento {
        PLANIFICACION,
        ACTIVO,
        FINALIZADO,
        CANCELADO
    }

    public Evento() {
        this.estado = EstadoEvento.PLANIFICACION;
        this.standsAsociados = new ArrayList<>();
    }

    // Getters y setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getLugar() {
        return lugar;
    }
    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public EstadoEvento getEstado() {
        return estado;
    }
    public void setEstado(EstadoEvento estado) {
        this.estado = estado;
    }

    public Integer getCapacidadMaximaStands() {
        return capacidadMaximaStands;
    }
    public void setCapacidadMaximaStands(Integer capacidadMaximaStands) {
        this.capacidadMaximaStands = capacidadMaximaStands;
    }

    public List<String> getStandsAsociados() {
        return standsAsociados;
    }
    public void setStandsAsociados(List<String> standsAsociados) {
        this.standsAsociados = standsAsociados;
    }

    public Double getLatitud() {
        return latitud;
    }
    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }
    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    // Métodos de utilidad
    public int getCantidadStandsAsociados() {
        return standsAsociados != null ? standsAsociados.size() : 0;
    }

    public boolean tieneCapacidadDisponible() {
        if (capacidadMaximaStands == null) return true;
        return getCantidadStandsAsociados() < capacidadMaximaStands;
    }
}
