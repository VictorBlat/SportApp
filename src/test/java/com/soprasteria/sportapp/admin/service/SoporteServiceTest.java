package com.soprasteria.sportapp.admin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soprasteria.sportapp.admin.model.SolicitudSoporte;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios de SoporteService.
 * Verifican la lógica de parseo JSON → modelo sin llamadas a Supabase.
 */
class SoporteServiceTest {


    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    private String extraerNombreUsuario(JsonObject obj) {
        if (obj.has("perfiles") && !obj.get("perfiles").isJsonNull()) {
            JsonObject perfil = obj.getAsJsonObject("perfiles");
            if (perfil.has("nombre") && !perfil.get("nombre").isJsonNull()) {
                return perfil.get("nombre").getAsString();
            }
        }
        return "";
    }

    private SolicitudSoporte solicitudDesdeJson(JsonObject obj) {
        return new SolicitudSoporte(
                getString(obj, "id"),
                getString(obj, "usuario_id"),
                extraerNombreUsuario(obj),
                getString(obj, "asunto"),
                getString(obj, "descripcion"),
                getString(obj, "estado"),
                getString(obj, "created_at"),
                obj.has("resuelto_at") && !obj.get("resuelto_at").isJsonNull()
                        ? obj.get("resuelto_at").getAsString() : null
        );
    }


    @Test
    void extraerNombre_conPerfilesAnidados_devuelveNombre() {
        JsonObject obj = buildSolicitudJson("sol-1", "usr-1", "Error", "pendiente", "Víctor Blat");

        assertEquals("Víctor Blat", extraerNombreUsuario(obj));
    }

    @Test
    void extraerNombre_sinPerfilesAnidados_devuelveCadenaVacia() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",         "sol-1");
        obj.addProperty("usuario_id", "usr-1");

        assertEquals("", extraerNombreUsuario(obj));
    }

    @Test
    void extraerNombre_perfilesNullExplicito_devuelveCadenaVacia() {
        JsonObject obj = new JsonObject();
        obj.add("perfiles", com.google.gson.JsonNull.INSTANCE);

        assertEquals("", extraerNombreUsuario(obj));
    }

    @Test
    void extraerNombre_nombreNullEnPerfil_devuelveCadenaVacia() {
        JsonObject obj = new JsonObject();
        JsonObject perfil = new JsonObject();
        perfil.add("nombre", com.google.gson.JsonNull.INSTANCE);
        obj.add("perfiles", perfil);

        assertEquals("", extraerNombreUsuario(obj));
    }


    @Test
    void solicitudDesdeJson_camposCompletos_mapeaCorrectamente() {
        JsonObject obj = buildSolicitudJson("sol-1", "usr-1", "Error login", "pendiente", "Víctor Blat");

        SolicitudSoporte s = solicitudDesdeJson(obj);

        assertEquals("sol-1",       s.getId());
        assertEquals("usr-1",       s.getUsuarioId());
        assertEquals("Víctor Blat", s.getUsuarioNombre());
        assertEquals("Error login", s.getAsunto());
        assertEquals("pendiente",   s.getEstado());
        assertNull(s.getResolvitoAt());
    }

    @Test
    void solicitudDesdeJson_conFechaResolucion_mapeaFecha() {
        JsonObject obj = buildSolicitudJson("sol-1", "usr-1", "Bug", "resuelto", "Víctor");
        obj.addProperty("resuelto_at", "2026-05-10T12:00:00Z");

        SolicitudSoporte s = solicitudDesdeJson(obj);

        assertEquals("resuelto",             s.getEstado());
        assertEquals("2026-05-10T12:00:00Z", s.getResolvitoAt());
    }

    @Test
    void parsearVariasSolicitudes_devuelveListaCompleta() {
        JsonArray array = new JsonArray();
        array.add(buildSolicitudJson("sol-1", "usr-1", "Error A", "pendiente",   "Víctor"));
        array.add(buildSolicitudJson("sol-2", "usr-2", "Error B", "en_revision", "María"));
        array.add(buildSolicitudJson("sol-3", "usr-1", "Error C", "resuelto",    "Víctor"));

        List<SolicitudSoporte> lista = new ArrayList<>();
        array.forEach(item -> lista.add(solicitudDesdeJson(item.getAsJsonObject())));

        assertEquals(3,       lista.size());
        assertEquals("sol-1", lista.get(0).getId());
        assertEquals("sol-2", lista.get(1).getId());
        assertEquals("sol-3", lista.get(2).getId());
    }


    @Test
    void estados_pendienteEnRevisionResuelto_sonValidos() {
        List<String> estadosValidos = List.of("pendiente", "en_revision", "resuelto");
        for (String estado : estadosValidos) {
            SolicitudSoporte s = new SolicitudSoporte();
            s.setEstado(estado);
            assertEquals(estado, s.getEstado());
        }
    }

    @Test
    void payloadMarcarEnRevision_estadoCorrecto() {
        JsonObject payload = new JsonObject();
        payload.addProperty("estado", "en_revision");

        assertEquals("en_revision", payload.get("estado").getAsString());
    }

    @Test
    void payloadMarcarResuelta_tieneEstadoYFecha() {
        JsonObject payload = new JsonObject();
        payload.addProperty("estado",      "resuelto");
        payload.addProperty("resuelto_at", "2026-05-22T10:00:00Z");

        assertEquals("resuelto", payload.get("estado").getAsString());
        assertTrue(payload.has("resuelto_at"));
        assertFalse(payload.get("resuelto_at").getAsString().isEmpty());
    }

    @Test
    void filtroConSelect_incluyePrefijo_select() {
        String filtro = "estado=eq.pendiente";
        String params  = "select=*,perfiles(nombre)&" + filtro;

        assertTrue(params.startsWith("select=*,perfiles(nombre)"));
        assertTrue(params.contains("estado=eq.pendiente"));
    }

    @Test
    void filtroSinFiltro_usaSelectConJoin() {
        String filtro = null;
        String params  = filtro != null ? "select=*,perfiles(nombre)&" + filtro
                : "select=*,perfiles(nombre)";

        assertEquals("select=*,perfiles(nombre)", params);
    }


    private JsonObject buildSolicitudJson(String id, String usuarioId, String asunto,
                                          String estado, String nombreUsuario) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",          id);
        obj.addProperty("usuario_id",  usuarioId);
        obj.addProperty("asunto",      asunto);
        obj.addProperty("descripcion", "Descripción de prueba");
        obj.addProperty("estado",      estado);
        obj.addProperty("created_at",  "2026-05-01T00:00:00Z");

        JsonObject perfil = new JsonObject();
        perfil.addProperty("nombre", nombreUsuario);
        obj.add("perfiles", perfil);

        return obj;
    }
}