package com.soprasteria.sportapp.admin.model;

/**
 * Modelo que representa un administrador del sistema.
 */
public class Administrador {

    private String id;
    private String email;
    private String nombre;
    private String createdAt;


    public Administrador() {
    }

    public Administrador(String id, String email, String nombre, String createdAt) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.createdAt = createdAt;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}