package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Evento.EstadoEvento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventoRepository extends MongoRepository<Evento, String> {
    List<Evento> findByEstado(EstadoEvento estado);
}