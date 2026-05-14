package com.soprasteria.sportapp.admin.model;

/**
 * Modelo que representa un usuario baneado por un administrador.
 */
public class UsuarioBaneado {

    private String id;
    private String usuarioId;
    private String usuarioNombre;
    private String motivo;
    private String adminId;
    private String adminNombre;
    private String fechaBaneo;
    private boolean activo;

    // Constructor vacío
    public UsuarioBaneado() {
    }

    // Constructor con parámetros
    public UsuarioBaneado(String id, String usuarioId, String usuarioNombre,
                         String motivo, String adminId, String adminNombre,
                         String fechaBaneo, boolean activo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.motivo = motivo;
        this.adminId = adminId;
        this.adminNombre = adminNombre;
        this.fechaBaneo = fechaBaneo;
        this.activo = activo;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminNombre() {
        return adminNombre;
    }

    public void setAdminNombre(String adminNombre) {
        this.adminNombre = adminNombre;
    }

    public String getFechaBaneo() {
        return fechaBaneo;
    }

    public void setFechaBaneo(String fechaBaneo) {
        this.fechaBaneo = fechaBaneo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "UsuarioBaneado{" +
                "id='" + id + '\'' +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", motivo='" + motivo + '\'' +
                ", fechaBaneo='" + fechaBaneo + '\'' +
                ", activo=" + activo +
                '}';
    }
}