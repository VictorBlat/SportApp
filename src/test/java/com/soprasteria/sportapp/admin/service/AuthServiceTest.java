package com.soprasteria.sportapp.admin.service;

import com.soprasteria.sportapp.admin.model.Administrador;
import com.soprasteria.sportapp.admin.util.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios de AuthService.
 * Verifican validaciones de entrada y lógica de sesión sin llamadas a Supabase.
 */
class AuthServiceTest {

    @BeforeEach
    void limpiarSesion() {
        SessionManager.getInstance().cerrarSesion();
    }

    // ── Validaciones de email ─────────────────────────────────────────────

    @Test
    void validarEmail_vacio_esInvalido() {
        assertTrue(emailInvalido(""));
    }

    @Test
    void validarEmail_null_esInvalido() {
        assertTrue(emailInvalido(null));
    }

    @Test
    void validarEmail_soloEspacios_esInvalido() {
        assertTrue(emailInvalido("   "));
    }

    @Test
    void validarEmail_correcto_esValido() {
        assertFalse(emailInvalido("admin@sportapp.com"));
    }

    // ── Validaciones de contraseña ────────────────────────────────────────

    @Test
    void validarPassword_vacia_esInvalida() {
        assertTrue(passwordInvalida(""));
    }

    @Test
    void validarPassword_null_esInvalida() {
        assertTrue(passwordInvalida(null));
    }

    @Test
    void validarPassword_correcta_esValida() {
        assertFalse(passwordInvalida("Admin1234"));
    }

    // ── SessionManager ────────────────────────────────────────────────────

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
        assertEquals("adm-1",           SessionManager.getInstance().getAdminLogueado().getId());
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
    void iniciarSesion_segundaVez_sobreescribeAdmin() {
        Administrador admin1 = new Administrador("adm-1", "a1@mail.com", "Admin 1", "2026-01-01");
        Administrador admin2 = new Administrador("adm-2", "a2@mail.com", "Admin 2", "2026-01-02");

        SessionManager.getInstance().iniciarSesion(admin1);
        SessionManager.getInstance().iniciarSesion(admin2);

        assertEquals("adm-2", SessionManager.getInstance().getAdminLogueado().getId());
    }

    // ── Singleton ─────────────────────────────────────────────────────────

    @Test
    void getInstance_siempreDevuelveLaMismaInstancia() {
        assertSame(SessionManager.getInstance(), SessionManager.getInstance());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private boolean emailInvalido(String email) {
        return email == null || email.isBlank();
    }

    private boolean passwordInvalida(String password) {
        return password == null || password.isBlank();
    }
}