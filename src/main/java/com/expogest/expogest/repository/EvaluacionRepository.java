package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Evaluacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EvaluacionRepository extends MongoRepository<Evaluacion, String> {
	List<Evaluacion> findByEvaluadorId(String evaluadorId);
	List<Evaluacion> findByEventoId(String eventoId);
	List<Evaluacion> findByEvaluadorIdAndEventoId(String evaluadorId, String eventoId);
	List<Evaluacion> findByEvaluadorIdAndStandIdAndEventoId(String evaluadorId, String standId, String eventoId);
}