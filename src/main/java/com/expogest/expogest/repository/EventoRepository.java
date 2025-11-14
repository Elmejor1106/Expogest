package com.expogest.expogest.repository;

import com.expogest.expogest.entidades.Evento;
import com.expogest.expogest.entidades.Evento.EstadoEvento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends MongoRepository<Evento, String> {
    List<Evento> findByEstado(EstadoEvento estado);
    
    // Búsqueda por nombre (case insensitive)
    List<Evento> findByNombreContainingIgnoreCase(String nombre);
    
    // Filtrar por rango de fechas
    List<Evento> findByFechaInicioBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Filtrar por estado y fecha de inicio
    List<Evento> findByEstadoAndFechaInicioAfter(EstadoEvento estado, LocalDate fecha);
    
    // Buscar eventos activos después de hoy
    List<Evento> findByEstadoAndFechaInicioAfterOrderByFechaInicioAsc(EstadoEvento estado, LocalDate fecha);
    
    // Buscar por descripción
    List<Evento> findByDescripcionContainingIgnoreCase(String descripcion);
}