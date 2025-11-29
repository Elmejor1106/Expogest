package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.Evaluacion;
import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.repository.EvaluacionRepository;
import com.expogest.expogest.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluacionServiceImpl implements EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final EventoRepository eventoRepository;

    public EvaluacionServiceImpl(EvaluacionRepository evaluacionRepository, EventoRepository eventoRepository) {
        this.evaluacionRepository = evaluacionRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public Evaluacion createEvaluacion(Evaluacion evaluacion) throws Exception {
        // Regla de negocio: Una evaluación por stand y evento por evaluador.
        List<Evaluacion> existingEvaluacion = evaluacionRepository.findByEvaluadorIdAndStandIdAndEventoId(
                evaluacion.getEvaluadorId(), evaluacion.getStandId(), evaluacion.getEventoId());

        if (!existingEvaluacion.isEmpty()) {
            throw new Exception("Ya existe una evaluación para este stand en este evento por el mismo evaluador.");
        }

        // Regla de negocio: La fecha del evento no ha finalizado
        Evento evento = eventoRepository.findById(evaluacion.getEventoId())
                .orElseThrow(() -> new Exception("El evento no existe."));

        if (evento.getFechaFin().isBefore(LocalDate.now())) {
            throw new Exception("El evento ya ha finalizado, no se pueden registrar más evaluaciones.");
        }


        // Regla de negocio: Las calificaciones se guardan automáticamente con fecha y hora.
        evaluacion.setFechaHora(LocalDateTime.now());

        return evaluacionRepository.save(evaluacion);
    }

    @Override
    public Evaluacion updateEvaluacion(String id, Evaluacion evaluacion) throws Exception {
        Evaluacion existingEvaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new Exception("La evaluación no existe."));

        // Regla de negocio: La fecha del evento no ha finalizado
        Evento evento = eventoRepository.findById(existingEvaluacion.getEventoId())
                .orElseThrow(() -> new Exception("El evento no existe."));

        if (evento.getFechaFin().isBefore(LocalDate.now())) {
            throw new Exception("El evento ya ha finalizado, no se pueden modificar las evaluaciones.");
        }

        existingEvaluacion.setNota(evaluacion.getNota());
        existingEvaluacion.setComentario(evaluacion.getComentario());
        existingEvaluacion.setFechaHora(LocalDateTime.now());

        return evaluacionRepository.save(existingEvaluacion);
    }

    @Override
    public Optional<Evaluacion> getEvaluacionById(@org.springframework.lang.NonNull String id) {
        return evaluacionRepository.findById(id);
    }

    @Override
    public List<Evaluacion> getEvaluacionesByEvento(String eventoId) {
        return evaluacionRepository.findByEventoId(eventoId);
    }
}
