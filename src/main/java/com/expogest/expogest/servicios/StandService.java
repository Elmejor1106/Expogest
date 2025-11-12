package com.expogest.expogest.servicios;

import com.expogest.expogest.entidades.Stand;
import com.expogest.expogest.entidades.Stand.EstadoStand;
import com.expogest.expogest.repository.StandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StandService {

    @Autowired
    private StandRepository standRepository;

    public List<Stand> obtenerTodos() {
        return standRepository.findAll();
    }

    public Optional<Stand> obtenerPorId(String id) {
        return standRepository.findById(id);
    }

    public Optional<Stand> obtenerPorNumero(String numero) {
        return standRepository.findByNumero(numero);
    }

    public Stand guardar(Stand stand) {
        // Validar que el número de stand sea único
        Optional<Stand> existente = standRepository.findByNumero(stand.getNumero());
        if (existente.isPresent()) {
            // Si está editando, permitir el mismo número solo si es el mismo stand
            if (stand.getId() == null || !existente.get().getId().equals(stand.getId())) {
                throw new IllegalArgumentException("Ya existe un stand con el número: " + stand.getNumero());
            }
        }

        // Establecer estado inicial si no tiene
        if (stand.getEstado() == null) {
            stand.setEstado(EstadoStand.DISPONIBLE);
        }

        return standRepository.save(stand);
    }

    public void eliminar(String id) {
        Optional<Stand> standOpt = standRepository.findById(id);
        if (standOpt.isPresent() && standOpt.get().getEventoId() != null) {
            throw new IllegalStateException("No se puede eliminar un stand asociado a un evento");
        }
        standRepository.deleteById(id);
    }

    public List<Stand> obtenerDisponibles() {
        return standRepository.findByEstado(EstadoStand.DISPONIBLE);
    }

    public List<Stand> obtenerPorEvento(String eventoId) {
        return standRepository.findByEventoId(eventoId);
    }

    public boolean asignarExpositor(String standId, String expositorId) {
        Optional<Stand> standOpt = standRepository.findById(standId);
        if (standOpt.isEmpty()) {
            return false;
        }

        Stand stand = standOpt.get();

        // Validar que el stand esté disponible o reservado
        if (stand.getEstado() != EstadoStand.DISPONIBLE && stand.getEstado() != EstadoStand.RESERVADO) {
            throw new IllegalStateException("El stand no está disponible para asignación");
        }

        // Asignar expositor y cambiar estado a OCUPADO
        stand.setExpositorId(expositorId);
        stand.setEstado(EstadoStand.OCUPADO);
        standRepository.save(stand);

        return true;
    }

    public boolean liberarStand(String standId) {
        Optional<Stand> standOpt = standRepository.findById(standId);
        if (standOpt.isEmpty()) {
            return false;
        }

        Stand stand = standOpt.get();
        stand.setExpositorId(null);
        
        // Si tiene evento asociado, volver a RESERVADO, sino DISPONIBLE
        if (stand.getEventoId() != null) {
            stand.setEstado(EstadoStand.RESERVADO);
        } else {
            stand.setEstado(EstadoStand.DISPONIBLE);
        }
        
        standRepository.save(stand);
        return true;
    }

    public void cambiarEstado(String standId, EstadoStand nuevoEstado) {
        Optional<Stand> standOpt = standRepository.findById(standId);
        if (standOpt.isPresent()) {
            Stand stand = standOpt.get();
            stand.setEstado(nuevoEstado);
            standRepository.save(stand);
        }
    }
}
