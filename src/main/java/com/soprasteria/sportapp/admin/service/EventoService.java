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



    public static String extraerNombreCreador(JsonObject obj) {
        if (obj.has("perfiles") && !obj.get("perfiles").isJsonNull()) {
            JsonObject perfil = obj.getAsJsonObject("perfiles");
            if (perfil.has("nombre") && !perfil.get("nombre").isJsonNull()) {
                return perfil.get("nombre").getAsString();
            }
        }
        return "";
    }

    private static String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    private static int getInt(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsInt() : 0;
    }

    private static double getDouble(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsDouble() : 0.0;
    }

    private static EventoActividad eventoDesdeJson(JsonObject obj) {
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

    /**
     * Obtiene la lista de todos los eventos de actividad.
     * Incluye el nombre del creador mediante join con perfiles.
     *
     * @param filtro Filtro opcional
     * @return CompletableFuture con lista de eventos
     */
    public static CompletableFuture<List<EventoActividad>> obtenerEventos(String filtro) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String params = filtro != null ? filtro : "select=*,perfiles(nombre)";
                JsonArray resultado = SupabaseService.getFromTable("evento_actividad", params).get();

                List<EventoActividad> eventos = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> eventos.add(eventoDesdeJson(item.getAsJsonObject())));
                }
                return eventos;
            } catch (Exception e) {
                throw new RuntimeException("Error obteniendo eventos: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Busca eventos por título o deporte.
     * Incluye el nombre del creador mediante join con perfiles.
     *
     * @param termino Término de búsqueda
     * @return CompletableFuture con lista de eventos filtrados
     */
    public static CompletableFuture<List<EventoActividad>> buscarEventos(String termino) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String filtro = "select=*,perfiles(nombre)&or=(titulo.ilike.%" + termino + "%,deporte.ilike.%" + termino + "%)";
                JsonArray resultado = SupabaseService.getFromTable("evento_actividad", filtro).get();

                List<EventoActividad> eventos = new ArrayList<>();
                if (resultado != null) {
                    resultado.forEach(item -> eventos.add(eventoDesdeJson(item.getAsJsonObject())));
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
                        "select=*,perfiles(nombre)&id=eq." + eventoId
                ).get();

                if (resultado == null || resultado.size() == 0) {
                    throw new Exception("Evento no encontrado");
                }

                return eventoDesdeJson(resultado.get(0).getAsJsonObject());
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
        return obtenerEventos("select=*,perfiles(nombre)&deporte=eq." + deporte);
    }

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
                                obj.has("emoji") ? getString(obj, "emoji") : "⭐",
                                getString(obj, "created_at")
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