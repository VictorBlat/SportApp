package com.soprasteria.sportapp.admin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    /**
     * Obtiene la lista de solicitudes de soporte.
     *
     * @param filtro Filtro opcional (ej: "estado=eq.pendiente")
     * @return CompletableFuture con lista de solicitudes
     */
    public static CompletableFuture<List<SolicitudSoporte>> obtenerSolicitudes(String filtro) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String params = filtro != null ? filtro : "select=*";
                JsonArray resultado = SupabaseService.getFromTable("solicitud_soporte", params).get();

                List<SolicitudSoporte> solicitudes = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> {
                        JsonObject obj = item.getAsJsonObject();
                        SolicitudSoporte solicitud = new SolicitudSoporte(
                                obj.get("id").getAsString(),
                                obj.get("usuario_id").getAsString(),
                                "",
                                obj.get("asunto").getAsString(),
                                obj.get("descripcion").getAsString(),
                                obj.get("estado").getAsString(),
                                obj.get("created_at").getAsString(),
                                obj.has("resuelto_at") ? obj.get("resuelto_at").getAsString() : null
                        );
                        solicitudes.add(solicitud);
                    });
                }
                return solicitudes;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo solicitudes: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Obtiene una solicitud por su ID.
     *
     * @param solicitudId ID de la solicitud
     * @return CompletableFuture con la solicitud
     */
    public static CompletableFuture<SolicitudSoporte> obtenerSolicitudPorId(String solicitudId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray resultado = SupabaseService.getFromTable(
                        "solicitud_soporte",
                        "id=eq." + solicitudId
                ).get();

                if (resultado == null || resultado.size() == 0) {
                    throw new Exception("Solicitud no encontrada");
                }

                JsonObject obj = resultado.get(0).getAsJsonObject();
                return new SolicitudSoporte(
                        obj.get("id").getAsString(),
                        obj.get("usuario_id").getAsString(),
                        "",
                        obj.get("asunto").getAsString(),
                        obj.get("descripcion").getAsString(),
                        obj.get("estado").getAsString(),
                        obj.get("created_at").getAsString(),
                        obj.has("resuelto_at") ? obj.get("resuelto_at").getAsString() : null
                );
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo solicitud: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Actualiza el estado de una solicitud.
     *
     * @param solicitudId ID de la solicitud
     * @param nuevoEstado Nuevo estado (pendiente, en_revision, resuelto)
     * @return CompletableFuture vacío
     */
    public static CompletableFuture<Void> actualizarEstado(String solicitudId, String nuevoEstado) {
        return CompletableFuture.runAsync(() -> {
            try {
                if (!esEstadoValido(nuevoEstado)) {
                    throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
                }

                JsonObject actualizado = new JsonObject();
                actualizado.addProperty("estado", nuevoEstado);

                // Si se marca como resuelto, añade timestamp
                if ("resuelto".equals(nuevoEstado)) {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                    actualizado.addProperty("resuelto_at", LocalDateTime.now().format(formatter));
                }

                SupabaseService.patchTable("solicitud_soporte", "id=eq." + solicitudId, actualizado).get();
            } catch (Exception e) {
                throw new RuntimeException("Error actualizando solicitud: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Marca una solicitud en revisión.
     *
     * @param solicitudId ID de la solicitud
     * @return CompletableFuture vacío
     */
    public static CompletableFuture<Void> marcarEnRevision(String solicitudId) {
        return actualizarEstado(solicitudId, "en_revision");
    }

    /**
     * Marca una solicitud como resuelta.
     *
     * @param solicitudId ID de la solicitud
     * @return CompletableFuture vacío
     */
    public static CompletableFuture<Void> marcarResuelta(String solicitudId) {
        return actualizarEstado(solicitudId, "resuelto");
    }

    /**
     * Obtiene el contador de solicitudes pendientes.
     *
     * @return CompletableFuture con el número de solicitudes pendientes
     */
    public static CompletableFuture<Integer> obtenerCountPendientes() {
        return obtenerSolicitudes("estado=eq.pendiente")
                .thenApply(List::size);
    }

    /**
     * Valida si el estado es válido.
     */
    private static boolean esEstadoValido(String estado) {
        return estado.equals("pendiente") ||
               estado.equals("en_revision") ||
               estado.equals("resuelto");
    }
}