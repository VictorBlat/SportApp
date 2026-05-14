package com.soprasteria.sportapp.admin.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soprasteria.sportapp.admin.model.Administrador;
import com.soprasteria.sportapp.admin.util.SessionManager;

import java.util.concurrent.CompletableFuture;

/**
 * Servicio de autenticación para administradores.
 * Verifica email + contraseña usando BCrypt.
 */
public class AuthService {

    /**
     * Realiza el login de un administrador.
     *
     * @param email    Email del administrador
     * @param password Contraseña en texto plano
     * @return CompletableFuture con el Administrador logueado
     */
    public static CompletableFuture<Administrador> login(String email, String password) {
        return CompletableFuture.supplyAsync(() -> {
            // Validar que email y contraseña no estén vacíos
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("El email no puede estar vacío");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña no puede estar vacía");
            }

            try {
                // Obtener el administrador de la tabla por email
                JsonArray resultado = SupabaseService.getFromTable(
                        "administradores",
                        "email=eq." + email
                ).get();

                if (resultado == null || resultado.size() == 0) {
                    throw new Exception("Administrador no encontrado");
                }

                JsonObject adminJson = resultado.get(0).getAsJsonObject();
                String passwordHash = adminJson.get("password_hash").getAsString();

                // Verificar contraseña con BCrypt
                BCrypt.Result resultado_bcrypt = BCrypt.verifyer().verify(
                        password.toCharArray(),
                        passwordHash
                );

                if (!resultado_bcrypt.verified) {
                    throw new Exception("Contraseña incorrecta");
                }

                // Crear objeto Administrador
                Administrador admin = new Administrador(
                        adminJson.get("id").getAsString(),
                        adminJson.get("email").getAsString(),
                        adminJson.get("nombre").getAsString(),
                        adminJson.get("created_at").getAsString()
                );

                // Guardar en sesión
                SessionManager.getInstance().iniciarSesion(admin);

                return admin;

            } catch (Exception e) {
                throw new RuntimeException("Error en login: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Cierra la sesión del administrador actual.
     */
    public static void logout() {
        SessionManager.getInstance().cerrarSesion();
    }

    /**
     * Verifica si hay una sesión activa.
     */
    public static boolean tieneSesionActiva() {
        return SessionManager.getInstance().tieneSesion();
    }

    /**
     * Obtiene el administrador actualmente logueado.
     */
    public static Administrador getAdminActual() {
        return SessionManager.getInstance().getAdminLogueado();
    }
}