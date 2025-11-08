package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.SolicitudStand;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SolicitudStandRepository extends MongoRepository<SolicitudStand, String> {
}