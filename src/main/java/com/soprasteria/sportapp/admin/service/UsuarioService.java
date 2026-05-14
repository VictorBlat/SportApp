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
                    resultado.forEach(item -> {
                        JsonObject obj = item.getAsJsonObject();
                        Usuario usuario = new Usuario(
                                obj.get("id").getAsString(),
                                obj.get("nombre").getAsString(),
                                obj.has("email") ? obj.get("email").getAsString() : "",
                                obj.get("ubicacion").getAsString(),
                                obj.get("created_at").getAsString(),
                                false // Por defecto no baneado
                        );
                        usuarios.add(usuario);
                    });
                }
                return usuarios;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo usuarios: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Busca usuarios por nombre o email.
     *
     * @param termino Término de búsqueda
     * @return CompletableFuture con lista de usuarios filtrados
     */
    public static CompletableFuture<List<Usuario>> buscarUsuarios(String termino) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String filtro = "or=(nombre.ilike.%" + termino + "%,email.ilike.%" + termino + "%)";
                JsonArray resultado = SupabaseService.getFromTable("perfiles", filtro).get();

                List<Usuario> usuarios = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> {
                        JsonObject obj = item.getAsJsonObject();
                        Usuario usuario = new Usuario(
                                obj.get("id").getAsString(),
                                obj.get("nombre").getAsString(),
                                obj.has("email") ? obj.get("email").getAsString() : "",
                                obj.get("ubicacion").getAsString(),
                                obj.get("created_at").getAsString(),
                                false
                        );
                        usuarios.add(usuario);
                    });
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
                        "perfiles",
                        "id=eq." + usuarioId
                ).get();

                if (resultado == null || resultado.size() == 0) {
                    throw new Exception("Usuario no encontrado");
                }

                JsonObject obj = resultado.get(0).getAsJsonObject();
                return new Usuario(
                        obj.get("id").getAsString(),
                        obj.get("nombre").getAsString(),
                        obj.has("email") ? obj.get("email").getAsString() : "",
                        obj.get("ubicacion").getAsString(),
                        obj.get("created_at").getAsString(),
                        false
                );
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo usuario: " + e.getMessage(), e);
            }
        });
    }

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
     * Desbanea un usuario.
     *
     * @param baneadoId ID del registro de baneo
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
     * Obtiene los baneos activos de un usuario.
     *
     * @param usuarioId ID del usuario
     * @return CompletableFuture con lista de baneos
     */
    public static CompletableFuture<List<UsuarioBaneado>> obtenerBaneos(String usuarioId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String filtro = "usuario_id=eq." + usuarioId + "&activo=eq.true";
                JsonArray resultado = SupabaseService.getFromTable("usuario_baneado", filtro).get();

                List<UsuarioBaneado> baneos = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> {
                        JsonObject obj = item.getAsJsonObject();
                        UsuarioBaneado baneo = new UsuarioBaneado(
                                obj.get("id").getAsString(),
                                obj.get("usuario_id").getAsString(),
                                "",
                                obj.get("motivo").getAsString(),
                                obj.get("admin_id").getAsString(),
                                "",
                                obj.get("fecha_baneo").getAsString(),
                                obj.get("activo").getAsBoolean()
                        );
                        baneos.add(baneo);
                    });
                }
                return baneos;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo baneos: " + e.getMessage(), e);
            }
        });
    }
}