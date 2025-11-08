package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Evaluacion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EvaluacionRepository extends MongoRepository<Evaluacion, String> {
}