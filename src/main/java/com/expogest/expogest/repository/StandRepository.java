package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Stand;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StandRepository extends MongoRepository<Stand, String> {
}