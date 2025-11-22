package com.expogest.expogest.repositorio;

import com.expogest.expogest.entidades.ActividadCronograma;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActividadCronogramaRepository extends MongoRepository<ActividadCronograma, String> {
    List<ActividadCronograma> findByEventoId(String eventoId);
    List<ActividadCronograma> findByEventoIdOrderByFechaHoraAsc(String eventoId);
    List<ActividadCronograma> findByEventoIdAndTipo(String eventoId, ActividadCronograma.TipoActividad tipo);
    List<ActividadCronograma> findByEventoIdAndFechaHoraBetween(String eventoId, LocalDateTime inicio, LocalDateTime fin);
    List<ActividadCronograma> findBySpeakerId(String speakerId);
}
