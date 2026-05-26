package com.soprasteria.sportapp.admin.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soprasteria.sportapp.admin.util.SupabaseConfig;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SupabaseService {

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    // Headers con anon key — sujeto a RLS (para operaciones normales)
    private static Headers.Builder getDefaultHeaders() {
        return new Headers.Builder()
                .add("Authorization", "Bearer " + SupabaseConfig.ANON_KEY)
                .add("apikey", SupabaseConfig.ANON_KEY)
                .add("Content-Type", "application/json");
    }

    // Headers con service role key — salta RLS (solo para estadísticas admin)
    private static Headers.Builder getAdminHeaders() {
        return new Headers.Builder()
                .add("Authorization", "Bearer " + SupabaseConfig.SERVICE_ROLE_KEY)
                .add("apikey", SupabaseConfig.SERVICE_ROLE_KEY)
                .add("Content-Type", "application/json");
    }

    // GET normal (con RLS)
    public static CompletableFuture<JsonArray> getFromTable(String table, String params) {
        return getFromTableWithHeaders(table, params, getDefaultHeaders());
    }

    // GET admin (sin RLS) — para estadísticas y conteos globales
    public static CompletableFuture<JsonArray> getFromTableAdmin(String table, String params) {
        return getFromTableWithHeaders(table, params, getAdminHeaders());
    }

    // GET con conteo exacto sin traer datos — devuelve JsonArray con un objeto {"count": N}
    public static CompletableFuture<JsonArray> countFromTableAdmin(String table) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = SupabaseConfig.REST_URL + "/" + table + "?select=id";

                Request request = new Request.Builder()
                        .url(url)
                        .headers(getAdminHeaders()
                                .add("Prefer", "count=exact")
                                .add("Range", "0-0")
                                .build())
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    // Supabase devuelve el total en el header Content-Range: 0-0/TOTAL
                    String contentRange = response.header("Content-Range", "0-0/0");
                    int total = 0;
                    if (contentRange != null && contentRange.contains("/")) {
                        try {
                            total = Integer.parseInt(contentRange.split("/")[1].trim());
                        } catch (NumberFormatException ignored) {}
                    }
                    JsonArray arr = new JsonArray();
                    JsonObject obj = new JsonObject();
                    obj.addProperty("count", total);
                    arr.add(obj);
                    return arr;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error contando " + table + ": " + e.getMessage(), e);
            }
        });
    }

    private static CompletableFuture<JsonArray> getFromTableWithHeaders(
            String table, String params, Headers.Builder headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = SupabaseConfig.REST_URL + "/" + table;
                if (params != null && !params.isBlank()) {
                    url += "?" + params;
                }

                Request request = new Request.Builder()
                        .url(url)
                        .headers(headers.build())
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

    // POST — inserta un registro
    public static CompletableFuture<JsonObject> postToTable(String table, JsonObject body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(SupabaseConfig.REST_URL + "/" + table)
                        .headers(getDefaultHeaders()
                                .add("Prefer", "return=representation")
                                .build())
                        .post(RequestBody.create(gson.toJson(body),
                                MediaType.parse("application/json")))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error HTTP: " + response.code());
                    }
                    String responseBody = response.body() != null ? response.body().string() : "[]";
                    JsonArray arr = gson.fromJson(responseBody, JsonArray.class);
                    return arr.size() > 0 ? arr.get(0).getAsJsonObject() : new JsonObject();
                }
            } catch (Exception e) {
                throw new RuntimeException("Error en POST " + table + ": " + e.getMessage(), e);
            }
        });
    }

    // PATCH — actualiza registros
    public static CompletableFuture<Void> patchTable(String table, String filter, JsonObject body) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = SupabaseConfig.REST_URL + "/" + table;
                if (filter != null && !filter.isBlank()) url += "?" + filter;

                Request request = new Request.Builder()
                        .url(url)
                        .headers(getDefaultHeaders().build())
                        .patch(RequestBody.create(gson.toJson(body),
                                MediaType.parse("application/json")))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error HTTP: " + response.code());
                    }
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error en PATCH " + table + ": " + e.getMessage(), e);
            }
        });
    }

    // DELETE — elimina registros
    public static CompletableFuture<Void> deleteFromTable(String table, String filter) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = SupabaseConfig.REST_URL + "/" + table;
                if (filter != null && !filter.isBlank()) url += "?" + filter;

                Request request = new Request.Builder()
                        .url(url)
                        .headers(getDefaultHeaders().build())
                        .delete()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error HTTP: " + response.code());
                    }
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error en DELETE " + table + ": " + e.getMessage(), e);
            }
        });
    }
}