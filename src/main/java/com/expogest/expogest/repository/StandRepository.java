package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.entidades.Stand.EstadoStand;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StandRepository extends MongoRepository<Stand, String> {
    Optional<Stand> findByNumero(String numero);
    List<Stand> findByEstado(EstadoStand estado);
    List<Stand> findByEventoId(String eventoId);
}