package com.soprasteria.sportapp.admin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soprasteria.sportapp.admin.model.Usuario;
import com.soprasteria.sportapp.admin.model.UsuarioBaneado;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para gestión de usuarios.
 * CRUD de usuarios y baneos.
 */
public class UsuarioService {

    // ── Helpers null-safe ──────────────────────────────────────────────────

    private static String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    private static int getInt(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsInt() : 0;
    }

    private static boolean getBoolean(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() && obj.get(key).getAsBoolean();
    }

    private static Usuario usuarioDesdeJson(JsonObject obj) {
        return new Usuario(
                getString(obj, "id"),
                getString(obj, "nombre"),
                getString(obj, "email"),
                getString(obj, "ubicacion"),
                getString(obj, "created_at"),
                false
        );
    }

    private static UsuarioBaneado baneadoDesdeJson(JsonObject obj) {
        return new UsuarioBaneado(
                getString(obj, "id"),
                getString(obj, "usuario_id"),
                "",
                getString(obj, "motivo"),
                getString(obj, "admin_id"),
                "",
                getString(obj, "fecha_baneo"),
                getBoolean(obj, "activo")
        );
    }

    // ── Usuarios ───────────────────────────────────────────────────────────

    /**
     * Obtiene la lista de todos los usuarios.
     *
     * @param filtro Filtro opcional (ej: "nombre=like.%Juan%")
     * @return CompletableFuture con lista de usuarios
     */
    public static CompletableFuture<List<Usuario>> obtenerUsuarios(String filtro) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String params = filtro != null ? filtro : "select=*";
                JsonArray resultado = SupabaseService.getFromTable("perfiles", params).get();

                List<Usuario> usuarios = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> usuarios.add(usuarioDesdeJson(item.getAsJsonObject())));
                }
                return usuarios;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo usuarios: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Busca usuarios por nombre.
     *
     * @param termino Término de búsqueda
     * @return CompletableFuture con lista de usuarios filtrados
     */
    public static CompletableFuture<List<Usuario>> buscarUsuarios(String termino) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String filtro = "select=*&nombre=ilike.%" + termino + "%";
                JsonArray resultado = SupabaseService.getFromTable("perfiles", filtro).get();

                List<Usuario> usuarios = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> usuarios.add(usuarioDesdeJson(item.getAsJsonObject())));
                }
                return usuarios;
            } catch (Exception e) {
                throw new RuntimeException("Error buscando usuarios: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param usuarioId ID del usuario
     * @return CompletableFuture con el usuario
     */
    public static CompletableFuture<Usuario> obtenerUsuarioPorId(String usuarioId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray resultado = SupabaseService.getFromTable(
                        "perfiles", "id=eq." + usuarioId
                ).get();

                if (resultado == null || resultado.isEmpty()) {
                    throw new Exception("Usuario no encontrado");
                }

                return usuarioDesdeJson(resultado.get(0).getAsJsonObject());
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo usuario: " + e.getMessage(), e);
            }
        });
    }

    // ── Baneos ─────────────────────────────────────────────────────────────

    /**
     * Banea un usuario.
     *
     * @param usuarioId ID del usuario a banear
     * @param motivo    Motivo del baneo
     * @param adminId   ID del administrador que banea
     * @return CompletableFuture vacío
     */
    public static CompletableFuture<Void> banearUsuario(String usuarioId, String motivo, String adminId) {
        return CompletableFuture.runAsync(() -> {
            try {
                JsonObject baneado = new JsonObject();
                baneado.addProperty("usuario_id", usuarioId);
                baneado.addProperty("motivo", motivo);
                baneado.addProperty("admin_id", adminId);
                baneado.addProperty("activo", true);

                SupabaseService.postToTable("usuario_baneado", baneado).get();
            } catch (Exception e) {
                throw new RuntimeException("Error baneando usuario: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Desbanea un usuario (marca el registro de baneo como inactivo).
     *
     * @param baneadoId ID del registro en usuario_baneado
     * @return CompletableFuture vacío
     */
    public static CompletableFuture<Void> desbanearUsuario(String baneadoId) {
        return CompletableFuture.runAsync(() -> {
            try {
                JsonObject actualizado = new JsonObject();
                actualizado.addProperty("activo", false);

                SupabaseService.patchTable("usuario_baneado", "id=eq." + baneadoId, actualizado).get();
            } catch (Exception e) {
                throw new RuntimeException("Error desbanendo usuario: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Obtiene los baneos activos de un usuario concreto.
     *
     * @param usuarioId ID del usuario
     * @return CompletableFuture con lista de baneos activos
     */
    public static CompletableFuture<List<UsuarioBaneado>> obtenerBaneos(String usuarioId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String filtro = "usuario_id=eq." + usuarioId + "&activo=eq.true";
                JsonArray resultado = SupabaseService.getFromTable("usuario_baneado", filtro).get();

                List<UsuarioBaneado> baneos = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> baneos.add(baneadoDesdeJson(item.getAsJsonObject())));
                }
                return baneos;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo baneos: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Obtiene todos los baneos activos (para el panel de usuarios).
     *
     * @return CompletableFuture con lista de todos los baneos activos
     */
    public static CompletableFuture<List<UsuarioBaneado>> obtenerTodosBaneos() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray resultado = SupabaseService.getFromTable("usuario_baneado", "activo=eq.true").get();

                List<UsuarioBaneado> baneos = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> baneos.add(baneadoDesdeJson(item.getAsJsonObject())));
                }
                return baneos;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo todos los baneos: " + e.getMessage(), e);
            }
        });
    }
}