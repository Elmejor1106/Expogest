package com.expogest.expogest.config;

import com.expogest.expogest.entidades.Usuario;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convertidor personalizado para transformar valores String del formulario HTML
 * al enum Usuario.Rol, permitiendo valores como "Administrador", "Organizador", etc.
 */
@Component
public class RolConverter implements Converter<String, Usuario.Rol> {

    @Override
    public Usuario.Rol convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        // Convertir a mayúsculas para coincidir con el enum
        String rolUpper = source.trim().toUpperCase();
        
        try {
            // Intentar conversión directa si viene en mayúsculas
            return Usuario.Rol.valueOf(rolUpper);
        } catch (IllegalArgumentException e) {
            // Si falla, intentar mapeo manual con capitalize
            switch (source.trim().toLowerCase()) {
                case "administrador":
                    return Usuario.Rol.ADMINISTRADOR;
                case "organizador":
                    return Usuario.Rol.ORGANIZADOR;
                case "expositor":
                    return Usuario.Rol.EXPOSITOR;
                case "visitante":
                    return Usuario.Rol.VISITANTE;
                case "evaluador":
                    return Usuario.Rol.EVALUADOR;
                default:
                    throw new IllegalArgumentException("Rol no válido: " + source);
            }
        }
    }
}
