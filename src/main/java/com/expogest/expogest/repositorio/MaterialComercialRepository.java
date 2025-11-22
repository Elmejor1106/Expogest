package com.expogest.expogest.repositorio;

import com.expogest.expogest.entidades.MaterialComercial;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MaterialComercialRepository extends MongoRepository<MaterialComercial, String> {
    Optional<MaterialComercial> findByExpositorId(String expositorId);
    Optional<MaterialComercial> findByStandId(String standId);
}
