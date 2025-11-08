package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    // Puedes agregar más métodos según necesidades
}