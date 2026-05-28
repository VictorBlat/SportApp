package com.soprasteria.sportapp.admin.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioBaneadoTest {

    @Test
    void constructor_conParametros_asignaCorrectamente() {
        UsuarioBaneado b = new UsuarioBaneado(
                "ban-1", "usr-1", "Víctor Blat",
                "Spam", "adm-1", "Admin Principal",
                "2026-05-01", true);

        assertEquals("ban-1",            b.getId());
        assertEquals("usr-1",            b.getUsuarioId());
        assertEquals("Víctor Blat",      b.getUsuarioNombre());
        assertEquals("Spam",             b.getMotivo());
        assertEquals("adm-1",            b.getAdminId());
        assertEquals("Admin Principal",  b.getAdminNombre());
        assertEquals("2026-05-01",       b.getFechaBaneo());
        assertTrue(b.isActivo());
    }

    @Test
    void constructorVacio_devuelveNulos() {
        UsuarioBaneado b = new UsuarioBaneado();
        assertNull(b.getId());
        assertNull(b.getMotivo());
        assertFalse(b.isActivo());
    }

    @Test
    void setActivo_false_marcaDesbaneo() {
        UsuarioBaneado b = new UsuarioBaneado();
        b.setActivo(true);
        assertTrue(b.isActivo());
        b.setActivo(false);
        assertFalse(b.isActivo());
    }

    @Test
    void toString_contieneNombreYMotivo() {
        UsuarioBaneado b = new UsuarioBaneado(
                "ban-1", "usr-1", "Víctor",
                "Conducta inapropiada", "adm-1", "",
                "2026-05-01", true);
        String str = b.toString();
        assertTrue(str.contains("Víctor"));
        assertTrue(str.contains("Conducta inapropiada"));
    }
}
