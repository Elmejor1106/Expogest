package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.Evaluacion;

import java.util.List;
import java.util.Optional;

public interface EvaluacionService {

    Evaluacion createEvaluacion(Evaluacion evaluacion) throws Exception;

    Evaluacion updateEvaluacion(String id, Evaluacion evaluacion) throws Exception;

    Optional<Evaluacion> getEvaluacionById(String id);

    List<Evaluacion> getEvaluacionesByEvento(String eventoId);

    // Otros métodos que puedas necesitar
}
