package com.expogest.expogest.config;

import com.expogest.expogest.entidades.Usuario;
import com.expogest.expogest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente que se ejecuta al iniciar la aplicación para crear un usuario administrador por defecto
 * si no existe ninguno en la base de datos.
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificar si ya existe un administrador
        boolean adminExiste = usuarioRepository.findAll().stream()
                .anyMatch(u -> u.getRol() == Usuario.Rol.ADMINISTRADOR);

        if (!adminExiste) {
            // Crear usuario admin por defecto
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo("admin@expogest.com");
            admin.setContrasena("admin123"); // En producción, usar BCrypt
            admin.setRol(Usuario.Rol.ADMINISTRADOR);
            
            usuarioRepository.save(admin);
            System.out.println("✓ Usuario administrador creado automáticamente");
            System.out.println("  Correo: admin@expogest.com");
            System.out.println("  Contraseña: admin123");
        } else {
            System.out.println("✓ Usuario administrador ya existe en la base de datos");
        }
    }
}
