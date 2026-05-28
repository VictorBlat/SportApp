package com.soprasteria.sportapp.admin.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdministradorTest {

    @Test
    void constructor_conParametros_asignaCorrectamente() {
        Administrador a = new Administrador(
                "adm-1", "admin@sportapp.com", "Admin Principal", "2026-01-01");

        assertEquals("adm-1",                a.getId());
        assertEquals("admin@sportapp.com",   a.getEmail());
        assertEquals("Admin Principal",      a.getNombre());
        assertEquals("2026-01-01",           a.getCreatedAt());
    }

    @Test
    void constructorVacio_devuelveNulos() {
        Administrador a = new Administrador();
        assertNull(a.getId());
        assertNull(a.getEmail());
        assertNull(a.getNombre());
    }

    @Test
    void setters_modificanCamposCorrectamente() {
        Administrador a = new Administrador();
        a.setId("adm-2");
        a.setEmail("otro@mail.com");
        a.setNombre("Otro Admin");
        a.setCreatedAt("2026-06-01");

        assertEquals("adm-2",          a.getId());
        assertEquals("otro@mail.com",  a.getEmail());
        assertEquals("Otro Admin",     a.getNombre());
        assertEquals("2026-06-01",     a.getCreatedAt());
    }

    @Test
    void toString_contieneEmailYNombre() {
        Administrador a = new Administrador(
                "adm-1", "admin@sportapp.com", "Admin Principal", "2026-01-01");
        String str = a.toString();
        assertTrue(str.contains("admin@sportapp.com"));
        assertTrue(str.contains("Admin Principal"));
    }
}
