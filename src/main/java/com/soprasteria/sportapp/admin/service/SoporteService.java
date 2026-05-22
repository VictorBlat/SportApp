package com.soprasteria.sportapp.admin.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.soprasteria.sportapp.admin.model.SolicitudSoporte;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para gestión de solicitudes de soporte.
 */
public class SoporteService {

    // ── Helper null-safe ───────────────────────────────────────────────────

    private static String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    private static String extraerNombreUsuario(JsonObject obj) {
        if (obj.has("perfiles") && !obj.get("perfiles").isJsonNull()) {
            JsonObject perfil = obj.getAsJsonObject("perfiles");
            if (perfil.has("nombre") && !perfil.get("nombre").isJsonNull()) {
                return perfil.get("nombre").getAsString();
            }
        }
        return "";
    }

    private static SolicitudSoporte solicitudDesdeJson(JsonObject obj) {
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

    // ── Métodos públicos ───────────────────────────────────────────────────

    /**
     * Obtiene la lista de solicitudes de soporte.
     *
     * @param filtro Filtro opcional (ej: "estado=eq.pendiente")
     */
    public static CompletableFuture<List<SolicitudSoporte>> obtenerSolicitudes(String filtro) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String params = filtro != null
                        ? "select=*,perfiles(nombre)&" + filtro
                        : "select=*,perfiles(nombre)";
                JsonArray resultado = SupabaseService.getFromTable("solicitud_soporte", params).get();

                List<SolicitudSoporte> solicitudes = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item ->
                            solicitudes.add(solicitudDesdeJson(item.getAsJsonObject())));
                }
                return solicitudes;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo solicitudes: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Obtiene una solicitud por su ID.
     */
    public static CompletableFuture<SolicitudSoporte> obtenerSolicitudPorId(String solicitudId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray resultado = SupabaseService.getFromTable(
                        "solicitud_soporte", "select=*,perfiles(nombre)&id=eq." + solicitudId).get();

                if (resultado == null || resultado.isEmpty()) {
                    throw new Exception("Solicitud no encontrada");
                }
                return solicitudDesdeJson(resultado.get(0).getAsJsonObject());
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo solicitud: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Actualiza el estado de una solicitud.
     */
    public static CompletableFuture<Void> actualizarEstado(String solicitudId, String nuevoEstado) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!esEstadoValido(nuevoEstado)) {
                    throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
                }

                JsonObject actualizado = new JsonObject();
                actualizado.addProperty("estado", nuevoEstado);

                if ("resuelto".equals(nuevoEstado)) {
                    actualizado.addProperty("resuelto_at",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                }

                SupabaseService.patchTable("solicitud_soporte", "id=eq." + solicitudId, actualizado).get();
            } catch (Exception e) {
                throw new RuntimeException("Error actualizando solicitud: " + e.getMessage(), e);
            }
        });
    }

    public static CompletableFuture<Void> marcarEnRevision(String solicitudId) {
        return actualizarEstado(solicitudId, "en_revision");
    }

    public static CompletableFuture<Void> marcarResuelta(String solicitudId) {
        return actualizarEstado(solicitudId, "resuelto");
    }

    public static CompletableFuture<Integer> obtenerCountPendientes() {
        return obtenerSolicitudes("estado=eq.pendiente").thenApply(List::size);
    }

    private static boolean esEstadoValido(String estado) {
        return "pendiente".equals(estado) || "en_revision".equals(estado) || "resuelto".equals(estado);
    }
}