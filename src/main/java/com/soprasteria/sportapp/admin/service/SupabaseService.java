package com.soprasteria.sportapp.admin.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soprasteria.sportapp.admin.util.SupabaseConfig;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio central que maneja todas las llamadas HTTP a Supabase.
 * Los demás servicios usan esta clase para interactuar con el backend.
 */
public class SupabaseService {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    /**
     * Construye los headers necesarios para las llamadas a Supabase.
     */
    private static Headers.Builder getDefaultHeaders() {
        return new Headers.Builder()
                .add("Authorization", "Bearer " + SupabaseConfig.ANON_KEY)
                .add("apikey", SupabaseConfig.ANON_KEY)
                .add("Content-Type", "application/json");
    }

    /**
     * Realiza una petición GET a una tabla de Supabase.
     *
     * @param table    Nombre de la tabla
     * @param params   Parámetros de filtrado (ej: "select=*" o "id=eq.123")
     * @return         CompletableFuture con el JsonArray de resultados
     */
    public static CompletableFuture<JsonArray> getFromTable(String table, String params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = SupabaseConfig.REST_URL + "/" + table;
                if (params != null && !params.isEmpty()) {
                    url += "?" + params;
                }

                Request request = new Request.Builder()
                        .url(url)
                        .headers(getDefaultHeaders().build())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error HTTP: " + response.code() + " - " + response.message());
                    }

                    String body = response.body() != null ? response.body().string() : "[]";
                    return gson.fromJson(body, JsonArray.class);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error en GET " + table + ": " + e.getMessage(), e);
            }
        });
    }

    /**
     * Realiza una petición POST a una tabla de Supabase.
     *
     * @param table    Nombre de la tabla
     * @param data     JsonObject con los datos a insertar
     * @return         CompletableFuture con el JsonObject insertado
     */
    public static CompletableFuture<JsonObject> postToTable(String table, JsonObject data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = SupabaseConfig.REST_URL + "/" + table;

                RequestBody body = RequestBody.create(
                        data.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(url)
                        .headers(getDefaultHeaders().build())
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error HTTP: " + response.code() + " - " + response.message());
                    }

                    String bodyResponse = response.body() != null ? response.body().string() : "{}";
                    JsonArray resultArray = gson.fromJson(bodyResponse, JsonArray.class);
                    return resultArray.size() > 0 ? resultArray.get(0).getAsJsonObject() : data;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error en POST " + table + ": " + e.getMessage(), e);
            }
        });
    }

    /**
     * Realiza una petición PATCH a una tabla de Supabase.
     *
     * @param table    Nombre de la tabla
     * @param filter   Filtro (ej: "id=eq.123")
     * @param data     JsonObject con los datos a actualizar
     * @return         CompletableFuture con el resultado
     */
    public static CompletableFuture<JsonArray> patchTable(String table, String filter, JsonObject data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = SupabaseConfig.REST_URL + "/" + table + "?" + filter;

                RequestBody body = RequestBody.create(
                        data.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(url)
                        .headers(getDefaultHeaders().build())
                        .patch(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error HTTP: " + response.code() + " - " + response.message());
                    }

                    String bodyResponse = response.body() != null ? response.body().string() : "[]";
                    return gson.fromJson(bodyResponse, JsonArray.class);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error en PATCH " + table + ": " + e.getMessage(), e);
            }
        });
    }

    /**
     * Realiza una petición DELETE a una tabla de Supabase.
     *
     * @param table    Nombre de la tabla
     * @param filter   Filtro (ej: "id=eq.123")
     * @return         CompletableFuture vacío
     */
    public static CompletableFuture<Void> deleteFromTable(String table, String filter) {
        return CompletableFuture.runAsync(() -> {
            try {
                String url = SupabaseConfig.REST_URL + "/" + table + "?" + filter;

                Request request = new Request.Builder()
                        .url(url)
                        .headers(getDefaultHeaders().build())
                        .delete()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error HTTP: " + response.code() + " - " + response.message());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error en DELETE " + table + ": " + e.getMessage(), e);
            }
        });
    }

    /**
     * Obtiene un Gson compartido para parsear JSON.
     */
    public static Gson getGson() {
        return gson;
    }
}