package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "solicitudes_stand")
public class SolicitudStand {
    @Id
    private String id;
    private String descripcion;
    private String expositorId;
    private String standId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getExpositorId() { return expositorId; }
    public void setExpositorId(String expositorId) { this.expositorId = expositorId; }

    public String getStandId() { return standId; }
    public void setStandId(String standId) { this.standId = standId; }
}