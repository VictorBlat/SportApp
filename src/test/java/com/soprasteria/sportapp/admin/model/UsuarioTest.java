package com.soprasteria.sportapp.admin.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {


    @Test
    void constructor_conParametros_asignaCorrectamente() {
        Usuario u = new Usuario("id-1", "Víctor Blat", "victor@mail.com",
                "Valencia", "2026-01-01", false);

        assertEquals("id-1",           u.getId());
        assertEquals("Víctor Blat",    u.getNombre());
        assertEquals("victor@mail.com",u.getEmail());
        assertEquals("Valencia",       u.getUbicacion());
        assertEquals("2026-01-01",     u.getCreatedAt());
        assertFalse(u.isBaneado());
    }

    @Test
    void constructorVacio_devuelveNulos() {
        Usuario u = new Usuario();
        assertNull(u.getId());
        assertNull(u.getNombre());
        assertFalse(u.isBaneado());
    }

    @Test
    void setters_modificanCamposCorrectamente() {
        Usuario u = new Usuario();
        u.setId("nuevo-id");
        u.setNombre("María García");
        u.setEmail("maria@mail.com");
        u.setUbicacion("Madrid");
        u.setCreatedAt("2026-05-01");
        u.setBaneado(true);

        assertEquals("nuevo-id",     u.getId());
        assertEquals("María García", u.getNombre());
        assertEquals("maria@mail.com", u.getEmail());
        assertEquals("Madrid",       u.getUbicacion());
        assertEquals("2026-05-01",   u.getCreatedAt());
        assertTrue(u.isBaneado());
    }


    @Test
    void toString_contieneNombreEId() {
        Usuario u = new Usuario("id-1", "Víctor Blat", "v@mail.com", null, null, false);
        String str = u.toString();
        assertTrue(str.contains("id-1"));
        assertTrue(str.contains("Víctor Blat"));
    }
}
