package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Cronograma;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CronogramaRepository extends MongoRepository<Cronograma, String> {
}