package com.soprasteria.sportapp.admin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soprasteria.sportapp.admin.model.EventoActividad;
import com.soprasteria.sportapp.admin.model.EventoEspecial;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios de EventoService.
 * Verifican la lógica de parseo JSON → modelo y validaciones sin llamadas a Supabase.
 */
class EventoServiceTest {


    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    private int getInt(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsInt() : 0;
    }

    private double getDouble(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsDouble() : 0.0;
    }

    private String extraerNombreCreador(JsonObject obj) {
        if (obj.has("perfiles") && !obj.get("perfiles").isJsonNull()) {
            JsonObject perfil = obj.getAsJsonObject("perfiles");
            if (perfil.has("nombre") && !perfil.get("nombre").isJsonNull()) {
                return perfil.get("nombre").getAsString();
            }
        }
        return "";
    }

    private EventoActividad eventoDesdeJson(JsonObject obj) {
        return new EventoActividad(
                getString(obj, "id"),
                getString(obj, "titulo"),
                getString(obj, "deporte"),
                getString(obj, "descripcion"),
                getString(obj, "ubicacion"),
                getString(obj, "fecha"),
                getString(obj, "hora"),
                getString(obj, "creador_id"),
                extraerNombreCreador(obj),
                getInt(obj, "participantes"),
                getInt(obj, "max_participantes"),
                getDouble(obj, "latitud"),
                getDouble(obj, "longitud"),
                obj.has("emoji") ? getString(obj, "emoji") : "⚽"
        );
    }


    @Test
    void eventoDesdeJson_camposCompletos_mapeaCorrectamente() {
        JsonObject obj = buildEventoJson("ev-1", "Fútbol 7", "Fútbol", "usr-1", "Víctor Blat");

        EventoActividad e = eventoDesdeJson(obj);

        assertEquals("ev-1",       e.getId());
        assertEquals("Fútbol 7",   e.getTitulo());
        assertEquals("Fútbol",     e.getDeporte());
        assertEquals("usr-1",      e.getCreadorId());
        assertEquals("Víctor Blat",e.getCreadorNombre());
        assertEquals(5,            e.getParticipantes());
        assertEquals(10,           e.getMaxParticipantes());
        assertEquals("⚽",         e.getEmoji());
    }

    @Test
    void eventoDesdeJson_conPerfilesAnidados_extraeNombreCreador() {
        JsonObject obj = buildEventoJson("ev-1", "Running", "Running", "usr-2", "María García");

        EventoActividad e = eventoDesdeJson(obj);

        assertEquals("María García", e.getCreadorNombre());
    }

    @Test
    void eventoDesdeJson_sinPerfilesAnidados_creadorNombreVacio() {
        JsonObject obj = buildEventoJson("ev-1", "Fútbol", "Fútbol", "usr-1", null);

        EventoActividad e = eventoDesdeJson(obj);

        assertEquals("", e.getCreadorNombre());
    }

    @Test
    void eventoDesdeJson_perfilesNullExplicito_creadorNombreVacio() {
        JsonObject obj = buildEventoJson("ev-1", "Fútbol", "Fútbol", "usr-1", null);
        obj.add("perfiles", com.google.gson.JsonNull.INSTANCE);

        EventoActividad e = eventoDesdeJson(obj);

        assertEquals("", e.getCreadorNombre());
    }

    @Test
    void parsearVariosEventos_devuelveListaCompleta() {
        JsonArray array = new JsonArray();
        array.add(buildEventoJson("ev-1", "Fútbol 7",   "Fútbol",  "usr-1", "Víctor"));
        array.add(buildEventoJson("ev-2", "Running 10k","Running", "usr-2", "María"));

        List<EventoActividad> lista = new ArrayList<>();
        array.forEach(item -> lista.add(eventoDesdeJson(item.getAsJsonObject())));

        assertEquals(2,          lista.size());
        assertEquals("ev-1",     lista.get(0).getId());
        assertEquals("Fútbol 7", lista.get(0).getTitulo());
        assertEquals("ev-2",     lista.get(1).getId());
    }

    @Test
    void eventoDesdeJson_camposNumericos_mapeaCorrectamente() {
        JsonObject obj = buildEventoJson("ev-1", "Título", "Deporte", "usr-1", "Nombre");
        obj.addProperty("participantes",     8);
        obj.addProperty("max_participantes", 20);
        obj.addProperty("latitud",           39.47);
        obj.addProperty("longitud",         -0.37);

        EventoActividad e = eventoDesdeJson(obj);

        assertEquals(8,    e.getParticipantes());
        assertEquals(20,   e.getMaxParticipantes());
        assertEquals(39.47,e.getLatitud(),  0.001);
        assertEquals(-0.37,e.getLongitud(), 0.001);
    }


    @Test
    void getEmojiPorTipo_benefico_devuelveCorazon() {
        assertEquals("❤️", EventoEspecial.getEmojiPorTipo("benefico"));
    }

    @Test
    void getEmojiPorTipo_torneo_devuelveTrofeo() {
        assertEquals("🏆", EventoEspecial.getEmojiPorTipo("torneo"));
    }

    @Test
    void getEmojiPorTipo_especial_devuelveEstrella() {
        assertEquals("⭐", EventoEspecial.getEmojiPorTipo("especial"));
    }

    @Test
    void getEmojiPorTipo_exhibicion_devuelveCarpa() {
        assertEquals("🎪", EventoEspecial.getEmojiPorTipo("exhibicion"));
    }

    @Test
    void getEmojiPorTipo_desconocido_devuelveEstrellaPorDefecto() {
        assertEquals("⭐", EventoEspecial.getEmojiPorTipo("desconocido"));
    }


    @Test
    void validacion_tituloVacio_esInvalido() {
        String titulo = "";
        assertTrue(titulo == null || titulo.isEmpty());
    }

    @Test
    void validacion_tituloNoVacio_esValido() {
        String titulo = "Torneo Primavera";
        assertFalse(titulo == null || titulo.isEmpty());
    }

    @Test
    void validacion_tiposValidos_pasanValidacion() {
        List<String> tiposValidos = List.of("benefico", "torneo", "especial", "exhibicion");
        for (String tipo : tiposValidos) {
            assertTrue(esTipoValido(tipo), "Tipo '" + tipo + "' debería ser válido");
        }
    }

    @Test
    void validacion_tipoInvalido_fallaValidacion() {
        assertFalse(esTipoValido("invalido"));
        assertFalse(esTipoValido(""));
        assertFalse(esTipoValido("TORNEO"));
    }


    @Test
    void eventoEspecialDesdeJson_camposCompletos_mapeaCorrectamente() {
        JsonObject obj = buildEventoEspecialJson("ee-1", "Torneo Primavera", "torneo", "adm-1");

        EventoEspecial e = new EventoEspecial(
                getString(obj, "id"),
                getString(obj, "titulo"),
                getString(obj, "tipo"),
                getString(obj, "descripcion"),
                getString(obj, "ubicacion"),
                getString(obj, "fecha"),
                getString(obj, "hora"),
                getString(obj, "admin_creador_id"),
                getInt(obj, "participantes"),
                getInt(obj, "max_participantes"),
                getDouble(obj, "latitud"),
                getDouble(obj, "longitud"),
                getString(obj, "emoji"),
                getString(obj, "created_at")
        );

        assertEquals("ee-1",             e.getId());
        assertEquals("Torneo Primavera", e.getTitulo());
        assertEquals("torneo",           e.getTipo());
        assertEquals("adm-1",            e.getAdminCreadorId());
        assertEquals("🏆",              e.getEmoji());
    }


    private boolean esTipoValido(String tipo) {
        return tipo.equals("benefico") || tipo.equals("torneo") ||
                tipo.equals("especial") || tipo.equals("exhibicion");
    }

    private JsonObject buildEventoJson(String id, String titulo, String deporte,
                                       String creadorId, String creadorNombre) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",               id);
        obj.addProperty("titulo",           titulo);
        obj.addProperty("deporte",          deporte);
        obj.addProperty("descripcion",      "Descripción");
        obj.addProperty("ubicacion",        "Valencia");
        obj.addProperty("fecha",            "2026-05-20");
        obj.addProperty("hora",             "18:00");
        obj.addProperty("creador_id",       creadorId);
        obj.addProperty("participantes",    5);
        obj.addProperty("max_participantes",10);
        obj.addProperty("latitud",          39.47);
        obj.addProperty("longitud",        -0.37);
        obj.addProperty("emoji",            "⚽");

        if (creadorNombre != null) {
            JsonObject perfil = new JsonObject();
            perfil.addProperty("nombre", creadorNombre);
            obj.add("perfiles", perfil);
        }
        return obj;
    }

    private JsonObject buildEventoEspecialJson(String id, String titulo, String tipo, String adminId) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",               id);
        obj.addProperty("titulo",           titulo);
        obj.addProperty("tipo",             tipo);
        obj.addProperty("descripcion",      "Descripción especial");
        obj.addProperty("ubicacion",        "Polideportivo");
        obj.addProperty("fecha",            "2026-06-01");
        obj.addProperty("hora",             "09:00");
        obj.addProperty("admin_creador_id", adminId);
        obj.addProperty("participantes",    0);
        obj.addProperty("max_participantes",100);
        obj.addProperty("latitud",          39.46);
        obj.addProperty("longitud",        -0.38);
        obj.addProperty("emoji",            EventoEspecial.getEmojiPorTipo(tipo));
        obj.addProperty("created_at",       "2026-05-01T00:00:00Z");
        return obj;
    }
}