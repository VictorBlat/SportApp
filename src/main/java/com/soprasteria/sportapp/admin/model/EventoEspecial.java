package com.soprasteria.sportapp.admin.model;

/**
 * Modelo que representa un evento especial creado solo por administradores.
 */
public class EventoEspecial {

    private String id;
    private String titulo;
    private String tipo; // 'benefico', 'torneo', 'especial', 'exhibicion'
    private String descripcion;
    private String ubicacion;
    private String fecha;
    private String hora;
    private String adminCreadorId;
    private int participantes;
    private int maxParticipantes;
    private double latitud;
    private double longitud;
    private String emoji;
    private String createdAt;

    // Constructor vacío
    public EventoEspecial() {
    }

    // Constructor con parámetros
    public EventoEspecial(String id, String titulo, String tipo, String descripcion,
                         String ubicacion, String fecha, String hora, String adminCreadorId,
                         int participantes, int maxParticipantes, double latitud,
                         double longitud, String emoji, String createdAt) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.hora = hora;
        this.adminCreadorId = adminCreadorId;
        this.participantes = participantes;
        this.maxParticipantes = maxParticipantes;
        this.latitud = latitud;
        this.longitud = longitud;
        this.emoji = emoji;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getAdminCreadorId() {
        return adminCreadorId;
    }

    public void setAdminCreadorId(String adminCreadorId) {
        this.adminCreadorId = adminCreadorId;
    }

    public int getParticipantes() {
        return participantes;
    }

    public void setParticipantes(int participantes) {
        this.participantes = participantes;
    }

    public int getMaxParticipantes() {
        return maxParticipantes;
    }

    public void setMaxParticipantes(int maxParticipantes) {
        this.maxParticipantes = maxParticipantes;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obtiene el emoji basado en el tipo del evento.
     */
    public static String getEmojiPorTipo(String tipo) {
        return switch (tipo) {
            case "benefico" -> "❤️";
            case "torneo" -> "🏆";
            case "especial" -> "⭐";
            case "exhibicion" -> "🎪";
            default -> "⭐";
        };
    }

    @Override
    public String toString() {
        return "EventoEspecial{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", participantes=" + participantes +
                ", maxParticipantes=" + maxParticipantes +
                '}';
    }
}