package com.expogest.expogest.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;
    private String nombre;
    
    @Indexed(unique = true)
    private String correo;
    
    private String contrasena;
    private Rol rol;

    public enum Rol {
        ADMINISTRADOR,
        VISITANTE,
        ORGANIZADOR,
        EXPOSITOR,
        EVALUADOR
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    
    // Método adicional para setear rol desde String (para formularios HTML)
    public void setRol(String rolString) {
        if (rolString != null && !rolString.isEmpty()) {
            try {
                this.rol = Rol.valueOf(rolString.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Si no encuentra el valor exacto, intenta con los valores disponibles
                switch (rolString.toLowerCase()) {
                    case "administrador":
                        this.rol = Rol.ADMINISTRADOR;
                        break;
                    case "organizador":
                        this.rol = Rol.ORGANIZADOR;
                        break;
                    case "expositor":
                        this.rol = Rol.EXPOSITOR;
                        break;
                    case "visitante":
                        this.rol = Rol.VISITANTE;
                        break;
                    case "evaluador":
                        this.rol = Rol.EVALUADOR;
                        break;
                    default:
                        this.rol = null;
                }
            }
        }
    }
}
