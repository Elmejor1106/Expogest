package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "participaciones")
public class Participacion {
    @Id
    private String id;
    private String usuarioId;
    private String eventoId;
    private String tipoParticipacion;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getEventoId() { return eventoId; }
    public void setEventoId(String eventoId) { this.eventoId = eventoId; }

    public String getTipoParticipacion() { return tipoParticipacion; }
    public void setTipoParticipacion(String tipoParticipacion) { this.tipoParticipacion = tipoParticipacion; }
}
