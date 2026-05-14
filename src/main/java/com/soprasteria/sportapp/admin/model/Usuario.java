package com.soprasteria.sportapp.admin.model;

/**
 * Modelo que representa un usuario de la app móvil.
 */
public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private String ubicacion;
    private String createdAt;
    private boolean baneado;

    // Constructor vacío
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String id, String nombre, String email, String ubicacion, String createdAt, boolean baneado) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.ubicacion = ubicacion;
        this.createdAt = createdAt;
        this.baneado = baneado;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isBaneado() {
        return baneado;
    }

    public void setBaneado(boolean baneado) {
        this.baneado = baneado;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", baneado=" + baneado +
                '}';
    }
}