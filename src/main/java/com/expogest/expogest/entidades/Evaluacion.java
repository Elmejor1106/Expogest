package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "evaluaciones")
public class Evaluacion {
    @Id
    private String id;
    private String evaluadorId;
    private String standId;
    private Double nota;

    private String eventoId;
    private String comentario;
    private LocalDateTime fechaHora;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEvaluadorId() { return evaluadorId; }
    public void setEvaluadorId(String evaluadorId) { this.evaluadorId = evaluadorId; }

    public String getStandId() { return standId; }
    public void setStandId(String standId) { this.standId = standId; }

    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }
    
    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
