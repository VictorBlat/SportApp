package com.soprasteria.sportapp.admin.model;

/**
 * Modelo que representa las estadísticas generales del sistema.
 */
public class Estadisticas {

    private int totalUsuarios;
    private int totalEventos;
    private int eventosActivos;
    private int totalMensajes;
    private double ingresosTransacciones;

    public Estadisticas() {
    }

    public Estadisticas(int totalUsuarios, int totalEventos, int eventosActivos,
                       int totalMensajes, double ingresosTransacciones) {
        this.totalUsuarios = totalUsuarios;
        this.totalEventos = totalEventos;
        this.eventosActivos = eventosActivos;
        this.totalMensajes = totalMensajes;
        this.ingresosTransacciones = ingresosTransacciones;
    }


    public int getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(int totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public int getTotalEventos() {
        return totalEventos;
    }

    public void setTotalEventos(int totalEventos) {
        this.totalEventos = totalEventos;
    }

    public int getEventosActivos() {
        return eventosActivos;
    }

    public void setEventosActivos(int eventosActivos) {
        this.eventosActivos = eventosActivos;
    }

    public int getTotalMensajes() {
        return totalMensajes;
    }

    public void setTotalMensajes(int totalMensajes) {
        this.totalMensajes = totalMensajes;
    }

    public double getIngresosTransacciones() {
        return ingresosTransacciones;
    }

    public void setIngresosTransacciones(double ingresosTransacciones) {
        this.ingresosTransacciones = ingresosTransacciones;
    }

    @Override
    public String toString() {
        return "Estadisticas{" +
                "totalUsuarios=" + totalUsuarios +
                ", totalEventos=" + totalEventos +
                ", eventosActivos=" + eventosActivos +
                ", totalMensajes=" + totalMensajes +
                ", ingresosTransacciones=" + ingresosTransacciones +
                '}';
    }
}