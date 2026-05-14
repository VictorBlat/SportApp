package com.soprasteria.sportapp.admin.model;

/**
 * Modelo que representa un evento deportivo creado por usuarios.
 */
public class EventoActividad {

    private String id;
    private String titulo;
    private String deporte;
    private String descripcion;
    private String ubicacion;
    private String fecha;
    private String hora;
    private String creadorId;
    private String creadorNombre;
    private int participantes;
    private int maxParticipantes;
    private double latitud;
    private double longitud;
    private String emoji;

    // Constructor vacío
    public EventoActividad() {
    }

    // Constructor con parámetros
    public EventoActividad(String id, String titulo, String deporte, String descripcion,
                          String ubicacion, String fecha, String hora, String creadorId,
                          String creadorNombre, int participantes, int maxParticipantes,
                          double latitud, double longitud, String emoji) {
        this.id = id;
        this.titulo = titulo;
        this.deporte = deporte;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.hora = hora;
        this.creadorId = creadorId;
        this.creadorNombre = creadorNombre;
        this.participantes = participantes;
        this.maxParticipantes = maxParticipantes;
        this.latitud = latitud;
        this.longitud = longitud;
        this.emoji = emoji;
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

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
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

    public String getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(String creadorId) {
        this.creadorId = creadorId;
    }

    public String getCreadorNombre() {
        return creadorNombre;
    }

    public void setCreadorNombre(String creadorNombre) {
        this.creadorNombre = creadorNombre;
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

    @Override
    public String toString() {
        return "EventoActividad{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", deporte='" + deporte + '\'' +
                ", fecha='" + fecha + '\'' +
                ", hora='" + hora + '\'' +
                ", creadorNombre='" + creadorNombre + '\'' +
                ", participantes=" + participantes +
                ", maxParticipantes=" + maxParticipantes +
                '}';
    }
}