package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Participacion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParticipacionRepository extends MongoRepository<Participacion, String> {
}