package com.soprasteria.sportapp.admin.util;

import com.soprasteria.sportapp.admin.model.Administrador;

/**
 * Singleton que mantiene en memoria la sesión del administrador logueado.
 * Si es null, redirige siempre a login.fxml.
 */
public class SessionManager {

    private static SessionManager instance;
    private Administrador adminLogueado;

    private SessionManager() {
        this.adminLogueado = null;
    }

    /**
     * Obtiene la instancia única del SessionManager.
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Inicia una nueva sesión con el administrador.
     */
    public void iniciarSesion(Administrador admin) {
        this.adminLogueado = admin;
    }

    /**
     * Cierra la sesión actual limpiando la referencia.
     */
    public void cerrarSesion() {
        this.adminLogueado = null;
    }

    /**
     * Obtiene el administrador logueado.
     *
     * @return El Administrador logueado o null si no hay sesión
     */
    public Administrador getAdminLogueado() {
        return adminLogueado;
    }

    /**
     * Verifica si hay una sesión activa.
     */
    public boolean tieneSesion() {
        return adminLogueado != null;
    }
}