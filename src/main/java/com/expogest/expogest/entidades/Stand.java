package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "stands")
public class Stand {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String numero; // Número único del stand
    
    private String nombre;
    private String ubicacion;
    private String dimensiones; // Ej: "3x3 metros"
    private Double precio;
    private EstadoStand estado;
    private String eventoId; // ID del evento al que está asociado
    private String expositorId; // ID del expositor asignado

    public enum EstadoStand {
        DISPONIBLE,
        RESERVADO,
        OCUPADO,
        MANTENIMIENTO
    }

    public Stand() {
        this.estado = EstadoStand.DISPONIBLE;
    }

    // Getters y setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDimensiones() {
        return dimensiones;
    }
    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public Double getPrecio() {
        return precio;
    }
    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public EstadoStand getEstado() {
        return estado;
    }
    public void setEstado(EstadoStand estado) {
        this.estado = estado;
    }

    public String getEventoId() {
        return eventoId;
    }
    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }

    public String getExpositorId() {
        return expositorId;
    }
    public void setExpositorId(String expositorId) {
        this.expositorId = expositorId;
    }

    // Métodos de utilidad
    public boolean estaDisponible() {
        return this.estado == EstadoStand.DISPONIBLE;
    }

    public boolean estaOcupado() {
        return this.estado == EstadoStand.OCUPADO || this.estado == EstadoStand.RESERVADO;
    }
}
