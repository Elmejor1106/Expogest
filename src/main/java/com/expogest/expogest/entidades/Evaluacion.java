package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "evaluaciones")
public class Evaluacion {
    @Id
    private String id;
    private String evaluadorId;
    private String standId;
    private Double nota;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEvaluadorId() { return evaluadorId; }
    public void setEvaluadorId(String evaluadorId) { this.evaluadorId = evaluadorId; }

    public String getStandId() { return standId; }
    public void setStandId(String standId) { this.standId = standId; }

    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }
}
