package com.soprasteria.sportapp.admin.model;

/**
 * Modelo que representa una solicitud de soporte enviada por un usuario.
 */
public class SolicitudSoporte {

    private String id;
    private String usuarioId;
    private String usuarioNombre;
    private String asunto;
    private String descripcion;
    private String estado; // 'pendiente', 'en_revision', 'resuelto'
    private String createdAt;
    private String resolvitoAt;

    // Constructor vacío
    public SolicitudSoporte() {
    }

    // Constructor con parámetros
    public SolicitudSoporte(String id, String usuarioId, String usuarioNombre,
                           String asunto, String descripcion, String estado,
                           String createdAt, String resolvitoAt) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.estado = estado;
        this.createdAt = createdAt;
        this.resolvitoAt = resolvitoAt;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getResolvitoAt() {
        return resolvitoAt;
    }

    public void setResolvitoAt(String resolvitoAt) {
        this.resolvitoAt = resolvitoAt;
    }

    @Override
    public String toString() {
        return "SolicitudSoporte{" +
                "id='" + id + '\'' +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", asunto='" + asunto + '\'' +
                ", estado='" + estado + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}