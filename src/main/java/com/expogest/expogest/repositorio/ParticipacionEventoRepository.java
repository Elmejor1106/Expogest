package com.expogest.expogest.repositorio;

import com.expogest.expogest.entidades.ParticipacionEvento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipacionEventoRepository extends MongoRepository<ParticipacionEvento, String> {
    List<ParticipacionEvento> findByEventoId(String eventoId);
    List<ParticipacionEvento> findByVisitanteId(String visitanteId);
    Optional<ParticipacionEvento> findByEventoIdAndVisitanteId(String eventoId, String visitanteId);
    long countByEventoIdAndEstado(String eventoId, ParticipacionEvento.EstadoParticipacion estado);
}
