package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventoRepository extends MongoRepository<Evento, String> {
}