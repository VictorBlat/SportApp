package com.soprasteria.sportapp.admin.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SolicitudSoporteTest {

    @Test
    void constructor_conParametros_asignaCorrectamente() {
        SolicitudSoporte s = new SolicitudSoporte(
                "sol-1", "usr-1", "Víctor Blat",
                "No puedo unirme", "Da error 403", "pendiente",
                "2026-05-01", null);

        assertEquals("sol-1",          s.getId());
        assertEquals("usr-1",          s.getUsuarioId());
        assertEquals("Víctor Blat",    s.getUsuarioNombre());
        assertEquals("No puedo unirme",s.getAsunto());
        assertEquals("Da error 403",   s.getDescripcion());
        assertEquals("pendiente",      s.getEstado());
        assertEquals("2026-05-01",     s.getCreatedAt());
        assertNull(s.getResolvitoAt());
    }

    @Test
    void constructorVacio_devuelveNulos() {
        SolicitudSoporte s = new SolicitudSoporte();
        assertNull(s.getId());
        assertNull(s.getEstado());
    }

    @Test
    void setEstado_cambiaCorrectamente() {
        SolicitudSoporte s = new SolicitudSoporte();
        s.setEstado("pendiente");
        assertEquals("pendiente", s.getEstado());

        s.setEstado("en_revision");
        assertEquals("en_revision", s.getEstado());

        s.setEstado("resuelto");
        assertEquals("resuelto", s.getEstado());
    }

    @Test
    void setUsuarioNombre_actualizaNombre() {
        SolicitudSoporte s = new SolicitudSoporte();
        s.setUsuarioNombre("María García");
        assertEquals("María García", s.getUsuarioNombre());
    }

    @Test
    void toString_contieneAsuntoYEstado() {
        SolicitudSoporte s = new SolicitudSoporte(
                "sol-1", "usr-1", "Víctor",
                "Error en chat", "No carga", "pendiente",
                "2026-05-01", null);
        String str = s.toString();
        assertTrue(str.contains("Error en chat"));
        assertTrue(str.contains("pendiente"));
    }
}
