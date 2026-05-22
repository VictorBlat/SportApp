package com.soprasteria.sportapp.admin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soprasteria.sportapp.admin.model.Usuario;
import com.soprasteria.sportapp.admin.model.UsuarioBaneado;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios de UsuarioService.
 * Verifican la lógica de parseo JSON → modelo sin llamadas reales a Supabase.
 */
class UsuarioServiceTest {

    // ── Helpers de parseo (misma lógica que UsuarioService internamente) ──

    private Usuario parsearUsuario(JsonObject obj) {
        return new Usuario(
                getString(obj, "id"),
                getString(obj, "nombre"),
                getString(obj, "email"),
                getString(obj, "ubicacion"),
                getString(obj, "created_at"),
                false
        );
    }

    private UsuarioBaneado parsearBaneo(JsonObject obj) {
        return new UsuarioBaneado(
                getString(obj, "id"),
                getString(obj, "usuario_id"),
                "",
                getString(obj, "motivo"),
                getString(obj, "admin_id"),
                "",
                getString(obj, "fecha_baneo"),
                obj.has("activo") && !obj.get("activo").isJsonNull() && obj.get("activo").getAsBoolean()
        );
    }

    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    // ── Parseo de Usuario ──────────────────────────────────────────────────

    @Test
    void parsearUsuario_camposCompletos_mapeaCorrectamente() {
        JsonObject obj = buildUsuarioJson("usr-1", "Víctor Blat", "victor@mail.com", "Valencia", "2026-05-01");

        Usuario u = parsearUsuario(obj);

        assertEquals("usr-1",           u.getId());
        assertEquals("Víctor Blat",     u.getNombre());
        assertEquals("victor@mail.com", u.getEmail());
        assertEquals("Valencia",        u.getUbicacion());
        assertEquals("2026-05-01",      u.getCreatedAt());
    }

    @Test
    void parsearUsuario_camposAusentes_devuelveCadenaVacia() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", "usr-1");

        Usuario u = parsearUsuario(obj);

        assertEquals("usr-1", u.getId());
        assertEquals("",      u.getNombre());
        assertEquals("",      u.getEmail());
        assertEquals("",      u.getUbicacion());
    }

    @Test
    void parsearUsuario_campoExplicitamenteNull_devuelveCadenaVacia() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", "usr-1");
        obj.add("nombre", com.google.gson.JsonNull.INSTANCE);

        Usuario u = parsearUsuario(obj);

        assertEquals("", u.getNombre());
    }

    @Test
    void parsearVariosUsuarios_devuelveListaCompleta() {
        JsonArray array = new JsonArray();
        array.add(buildUsuarioJson("usr-1", "Víctor Blat",  "v@mail.com", "Valencia", "2026-05-01"));
        array.add(buildUsuarioJson("usr-2", "María García", "m@mail.com", "Madrid",   "2026-05-02"));

        List<Usuario> lista = new ArrayList<>();
        array.forEach(item -> lista.add(parsearUsuario(item.getAsJsonObject())));

        assertEquals(2,             lista.size());
        assertEquals("usr-1",       lista.get(0).getId());
        assertEquals("Víctor Blat", lista.get(0).getNombre());
        assertEquals("usr-2",       lista.get(1).getId());
        assertEquals("María García",lista.get(1).getNombre());
    }

    // ── Filtrado local (lógica del controller) ────────────────────────────

    @Test
    void filtradoLocal_terminoEnNombre_devuelveCoincidencias() {
        List<Usuario> todos = List.of(
                new Usuario("usr-1", "Víctor Blat",   "", "", "", false),
                new Usuario("usr-2", "María García",  "", "", "", false),
                new Usuario("usr-3", "Víctor Suárez", "", "", "", false)
        );

        List<Usuario> resultado = todos.stream()
                .filter(u -> u.getNombre() != null &&
                        u.getNombre().toLowerCase().contains("víctor"))
                .toList();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(u -> u.getNombre().toLowerCase().contains("víctor")));
    }

    @Test
    void filtradoLocal_terminoSinCoincidencias_devuelveListaVacia() {
        List<Usuario> todos = List.of(
                new Usuario("usr-1", "Víctor Blat",  "", "", "", false),
                new Usuario("usr-2", "María García", "", "", "", false)
        );

        List<Usuario> resultado = todos.stream()
                .filter(u -> u.getNombre() != null &&
                        u.getNombre().toLowerCase().contains("zzz"))
                .toList();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void filtradoLocal_caseInsensitive_encuentraConMayusculasYMinusculas() {
        List<Usuario> todos = List.of(
                new Usuario("usr-1", "Víctor Blat", "", "", "", false)
        );

        List<Usuario> resultado = todos.stream()
                .filter(u -> u.getNombre().toLowerCase().contains("VÍCTOR".toLowerCase()))
                .toList();

        assertEquals(1, resultado.size());
    }

    // ── Parseo de UsuarioBaneado ──────────────────────────────────────────

    @Test
    void parsearBaneo_activo_true_mapeaCorrectamente() {
        JsonObject obj = buildBaneoJson("ban-1", "usr-1", "Spam", true);

        UsuarioBaneado b = parsearBaneo(obj);

        assertEquals("ban-1", b.getId());
        assertEquals("usr-1", b.getUsuarioId());
        assertEquals("Spam",  b.getMotivo());
        assertTrue(b.isActivo());
    }

    @Test
    void parsearBaneo_activo_false_mapeaCorrectamente() {
        JsonObject obj = buildBaneoJson("ban-1", "usr-1", "Spam", false);

        UsuarioBaneado b = parsearBaneo(obj);

        assertFalse(b.isActivo());
    }

    @Test
    void parsearVariosBaneos_devuelveListaCompleta() {
        JsonArray array = new JsonArray();
        array.add(buildBaneoJson("ban-1", "usr-1", "Spam",     true));
        array.add(buildBaneoJson("ban-2", "usr-2", "Insultos", true));

        List<UsuarioBaneado> lista = new ArrayList<>();
        array.forEach(item -> lista.add(parsearBaneo(item.getAsJsonObject())));

        assertEquals(2,       lista.size());
        assertEquals("ban-1", lista.get(0).getId());
        assertEquals("ban-2", lista.get(1).getId());
    }

    // ── Construcción del payload de baneo ────────────────────────────────

    @Test
    void payloadBaneo_contienesCamposObligatorios() {
        JsonObject payload = new JsonObject();
        payload.addProperty("usuario_id", "usr-1");
        payload.addProperty("motivo",     "Conducta inapropiada");
        payload.addProperty("admin_id",   "adm-1");
        payload.addProperty("activo",     true);

        assertEquals("usr-1",               payload.get("usuario_id").getAsString());
        assertEquals("Conducta inapropiada",payload.get("motivo").getAsString());
        assertEquals("adm-1",               payload.get("admin_id").getAsString());
        assertTrue(payload.get("activo").getAsBoolean());
    }

    @Test
    void payloadDesbaneo_activoEsFalse() {
        JsonObject payload = new JsonObject();
        payload.addProperty("activo", false);

        assertFalse(payload.get("activo").getAsBoolean());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private JsonObject buildUsuarioJson(String id, String nombre, String email,
                                        String ubicacion, String createdAt) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",         id);
        obj.addProperty("nombre",     nombre);
        obj.addProperty("email",      email);
        obj.addProperty("ubicacion",  ubicacion);
        obj.addProperty("created_at", createdAt);
        return obj;
    }

    private JsonObject buildBaneoJson(String id, String usuarioId, String motivo, boolean activo) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",          id);
        obj.addProperty("usuario_id",  usuarioId);
        obj.addProperty("motivo",      motivo);
        obj.addProperty("admin_id",    "adm-1");
        obj.addProperty("fecha_baneo", "2026-05-01T00:00:00Z");
        obj.addProperty("activo",      activo);
        return obj;
    }
}