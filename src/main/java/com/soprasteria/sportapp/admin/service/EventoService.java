package com.soprasteria.sportapp.admin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soprasteria.sportapp.admin.model.EventoActividad;
import com.soprasteria.sportapp.admin.model.EventoEspecial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para gestión de eventos.
 * CRUD de eventos normales y especiales.
 */
public class EventoService {

    /**
     * Obtiene la lista de todos los eventos de actividad.
     *
     * @param filtro Filtro opcional
     * @return CompletableFuture con lista de eventos
     */
    public static CompletableFuture<List<EventoActividad>> obtenerEventos(String filtro) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String params = filtro != null ? filtro : "select=*";
                JsonArray resultado = SupabaseService.getFromTable("evento_actividad", params).get();

                List<EventoActividad> eventos = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> {
                        JsonObject obj = item.getAsJsonObject();
                        EventoActividad evento = new EventoActividad(
                                obj.get("id").getAsString(),
                                obj.get("titulo").getAsString(),
                                obj.get("deporte").getAsString(),
                                obj.has("descripcion") ? obj.get("descripcion").getAsString() : "",
                                obj.get("ubicacion").getAsString(),
                                obj.get("fecha").getAsString(),
                                obj.get("hora").getAsString(),
                                obj.get("creador_id").getAsString(),
                                "",
                                obj.get("participantes").getAsInt(),
                                obj.get("max_participantes").getAsInt(),
                                obj.get("latitud").getAsDouble(),
                                obj.get("longitud").getAsDouble(),
                                obj.has("emoji") ? obj.get("emoji").getAsString() : "⚽"
                        );
                        eventos.add(evento);
                    });
                }
                return eventos;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo eventos: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Busca eventos por título o deporte.
     *
     * @param termino Término de búsqueda
     * @return CompletableFuture con lista de eventos filtrados
     */
    public static CompletableFuture<List<EventoActividad>> buscarEventos(String termino) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String filtro = "or=(titulo.ilike.%" + termino + "%,deporte.ilike.%" + termino + "%)";
                JsonArray resultado = SupabaseService.getFromTable("evento_actividad", filtro).get();

                List<EventoActividad> eventos = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> {
                        JsonObject obj = item.getAsJsonObject();
                        EventoActividad evento = new EventoActividad(
                                obj.get("id").getAsString(),
                                obj.get("titulo").getAsString(),
                                obj.get("deporte").getAsString(),
                                obj.has("descripcion") ? obj.get("descripcion").getAsString() : "",
                                obj.get("ubicacion").getAsString(),
                                obj.get("fecha").getAsString(),
                                obj.get("hora").getAsString(),
                                obj.get("creador_id").getAsString(),
                                "",
                                obj.get("participantes").getAsInt(),
                                obj.get("max_participantes").getAsInt(),
                                obj.get("latitud").getAsDouble(),
                                obj.get("longitud").getAsDouble(),
                                obj.has("emoji") ? obj.get("emoji").getAsString() : "⚽"
                        );
                        eventos.add(evento);
                    });
                }
                return eventos;
            } catch (Exception e) {
                throw new RuntimeException("Error buscando eventos: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Obtiene un evento por su ID.
     *
     * @param eventoId ID del evento
     * @return CompletableFuture con el evento
     */
    public static CompletableFuture<EventoActividad> obtenerEventoPorId(String eventoId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonArray resultado = SupabaseService.getFromTable(
                        "evento_actividad",
                        "id=eq." + eventoId
                ).get();

                if (resultado == null || resultado.size() == 0) {
                    throw new Exception("Evento no encontrado");
                }

                JsonObject obj = resultado.get(0).getAsJsonObject();
                return new EventoActividad(
                        obj.get("id").getAsString(),
                        obj.get("titulo").getAsString(),
                        obj.get("deporte").getAsString(),
                        obj.has("descripcion") ? obj.get("descripcion").getAsString() : "",
                        obj.get("ubicacion").getAsString(),
                        obj.get("fecha").getAsString(),
                        obj.get("hora").getAsString(),
                        obj.get("creador_id").getAsString(),
                        "",
                        obj.get("participantes").getAsInt(),
                        obj.get("max_participantes").getAsInt(),
                        obj.get("latitud").getAsDouble(),
                        obj.get("longitud").getAsDouble(),
                        obj.has("emoji") ? obj.get("emoji").getAsString() : "⚽"
                );
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo evento: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Elimina un evento.
     *
     * @param eventoId ID del evento
     * @return CompletableFuture vacío
     */
    public static CompletableFuture<Void> eliminarEvento(String eventoId) {
        return SupabaseService.deleteFromTable("evento_actividad", "id=eq." + eventoId);
    }

    /**
     * Filtra eventos por deporte.
     *
     * @param deporte Nombre del deporte
     * @return CompletableFuture con lista de eventos del deporte
     */
    public static CompletableFuture<List<EventoActividad>> filtrarPorDeporte(String deporte) {
        return obtenerEventos("deporte=eq." + deporte);
    }

    // ============= EVENTOS ESPECIALES =============

    /**
     * Obtiene la lista de todos los eventos especiales.
     *
     * @param filtro Filtro opcional
     * @return CompletableFuture con lista de eventos especiales
     */
    public static CompletableFuture<List<EventoEspecial>> obtenerEventosEspeciales(String filtro) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String params = filtro != null ? filtro : "select=*";
                JsonArray resultado = SupabaseService.getFromTable("evento_especial", params).get();

                List<EventoEspecial> eventos = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> {
                        JsonObject obj = item.getAsJsonObject();
                        EventoEspecial evento = new EventoEspecial(
                                obj.get("id").getAsString(),
                                obj.get("titulo").getAsString(),
                                obj.get("tipo").getAsString(),
                                obj.has("descripcion") ? obj.get("descripcion").getAsString() : "",
                                obj.get("ubicacion").getAsString(),
                                obj.get("fecha").getAsString(),
                                obj.get("hora").getAsString(),
                                obj.get("admin_creador_id").getAsString(),
                                obj.get("participantes").getAsInt(),
                                obj.get("max_participantes").getAsInt(),
                                obj.get("latitud").getAsDouble(),
                                obj.get("longitud").getAsDouble(),
                                obj.has("emoji") ? obj.get("emoji").getAsString() : "⭐",
                                obj.get("created_at").getAsString()
                        );
                        eventos.add(evento);
                    });
                }
                return eventos;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo eventos especiales: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Crea un nuevo evento especial.
     *
     * @param evento Evento especial a crear
     * @return CompletableFuture con el evento creado
     */
    public static CompletableFuture<EventoEspecial> crearEventoEspecial(EventoEspecial evento) {
        return CompletableFuture.supplyAsync(() -> {
            // Validación
            if (evento.getTitulo() == null || evento.getTitulo().isEmpty()) {
                throw new IllegalArgumentException("El título es obligatorio");
            }
            if (evento.getTipo() == null || evento.getTipo().isEmpty()) {
                throw new IllegalArgumentException("El tipo es obligatorio");
            }
            if (!estipoValido(evento.getTipo())) {
                throw new IllegalArgumentException("Tipo de evento inválido");
            }

            try {
                JsonObject data = new JsonObject();
                data.addProperty("titulo", evento.getTitulo());
                data.addProperty("tipo", evento.getTipo());
                data.addProperty("descripcion", evento.getDescripcion() != null ? evento.getDescripcion() : "");
                data.addProperty("ubicacion", evento.getUbicacion() != null ? evento.getUbicacion() : "");
                data.addProperty("fecha", evento.getFecha() != null ? evento.getFecha() : "");
                data.addProperty("hora", evento.getHora() != null ? evento.getHora() : "");
                data.addProperty("admin_creador_id", evento.getAdminCreadorId());
                data.addProperty("participantes", evento.getParticipantes());
                data.addProperty("max_participantes", evento.getMaxParticipantes());
                data.addProperty("latitud", evento.getLatitud());
                data.addProperty("longitud", evento.getLongitud());
                data.addProperty("emoji", EventoEspecial.getEmojiPorTipo(evento.getTipo()));

                JsonObject resultado = SupabaseService.postToTable("evento_especial", data).get();

                return new EventoEspecial(
                        resultado.get("id").getAsString(),
                        resultado.get("titulo").getAsString(),
                        resultado.get("tipo").getAsString(),
                        resultado.has("descripcion") ? resultado.get("descripcion").getAsString() : "",
                        resultado.get("ubicacion").getAsString(),
                        resultado.get("fecha").getAsString(),
                        resultado.get("hora").getAsString(),
                        resultado.get("admin_creador_id").getAsString(),
                        resultado.get("participantes").getAsInt(),
                        resultado.get("max_participantes").getAsInt(),
                        resultado.get("latitud").getAsDouble(),
                        resultado.get("longitud").getAsDouble(),
                        resultado.get("emoji").getAsString(),
                        resultado.get("created_at").getAsString()
                );
            } catch (Exception e) {
                throw new RuntimeException("Error creando evento especial: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Elimina un evento especial.
     *
     * @param eventoId ID del evento
     * @return CompletableFuture vacío
     */
    public static CompletableFuture<Void> eliminarEventoEspecial(String eventoId) {
        return SupabaseService.deleteFromTable("evento_especial", "id=eq." + eventoId);
    }

    /**
     * Valida si el tipo de evento es correcto.
     */
    private static boolean estipoValido(String tipo) {
        return tipo.equals("benefico") ||
               tipo.equals("torneo") ||
               tipo.equals("especial") ||
               tipo.equals("exhibicion");
    }
}