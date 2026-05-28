package com.soprasteria.sportapp.admin.util;

import com.soprasteria.sportapp.admin.model.Administrador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @BeforeEach
    void limpiarSesion() {
        SessionManager.getInstance().cerrarSesion();
    }

    @Test
    void getInstance_siempreDevuelveLaMismaInstancia() {
        SessionManager s1 = SessionManager.getInstance();
        SessionManager s2 = SessionManager.getInstance();
        assertSame(s1, s2);
    }


    @Test
    void sinSesion_tieneSesion_esFalse() {
        assertFalse(SessionManager.getInstance().tieneSesion());
    }

    @Test
    void sinSesion_getAdminLogueado_esNull() {
        assertNull(SessionManager.getInstance().getAdminLogueado());
    }


    @Test
    void iniciarSesion_guardaElAdmin() {
        Administrador admin = new Administrador(
                "adm-1", "admin@sportapp.com", "Admin Principal", "2026-01-01");

        SessionManager.getInstance().iniciarSesion(admin);

        assertTrue(SessionManager.getInstance().tieneSesion());
        assertEquals("adm-1", SessionManager.getInstance().getAdminLogueado().getId());
        assertEquals("Admin Principal", SessionManager.getInstance().getAdminLogueado().getNombre());
    }


    @Test
    void cerrarSesion_eliminaElAdmin() {
        Administrador admin = new Administrador(
                "adm-1", "admin@sportapp.com", "Admin Principal", "2026-01-01");
        SessionManager.getInstance().iniciarSesion(admin);

        SessionManager.getInstance().cerrarSesion();

        assertFalse(SessionManager.getInstance().tieneSesion());
        assertNull(SessionManager.getInstance().getAdminLogueado());
    }

    @Test
    void cerrarSesion_sinSesionPrevia_noLanzaExcepcion() {
        assertDoesNotThrow(() -> SessionManager.getInstance().cerrarSesion());
    }


    @Test
    void iniciarSesion_segundaVez_sobreescribeElAdmin() {
        Administrador admin1 = new Administrador("adm-1", "a1@mail.com", "Admin 1", "2026-01-01");
        Administrador admin2 = new Administrador("adm-2", "a2@mail.com", "Admin 2", "2026-01-02");

        SessionManager.getInstance().iniciarSesion(admin1);
        SessionManager.getInstance().iniciarSesion(admin2);

        assertEquals("adm-2", SessionManager.getInstance().getAdminLogueado().getId());
    }
}
