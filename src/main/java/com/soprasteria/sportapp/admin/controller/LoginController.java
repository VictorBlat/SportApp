package com.soprasteria.sportapp.admin.controller;

import com.soprasteria.sportapp.admin.service.AuthService;
import com.soprasteria.sportapp.admin.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Controlador de la pantalla de login.
 * Gestiona la autenticación de administradores.
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button botonAcceder;

    @FXML
    public void initialize() {
        // Permitir login con Enter
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    /**
     * Maneja el evento de login.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertHelper.mostrarError("Campos vacíos", "Por favor completa email y contraseña");
            return;
        }

        // Deshabilitar botón durante el login
        botonAcceder.setDisable(true);
        botonAcceder.setText("Accediendo...");

        // Realizar login en hilo separado
        AuthService.login(email, password)
                .thenAccept(admin -> Platform.runLater(() -> {
                    AlertHelper.mostrarInfo("Login exitoso", "Bienvenido " + admin.getNombre());
                    navToDashboard();
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        botonAcceder.setDisable(false);
                        botonAcceder.setText("Acceder");
                        AlertHelper.mostrarError("Error de login", e.getMessage());
                    });
                    return null;
                });
    }

    /**
     * Navega a la pantalla del dashboard.
     */
    private void navToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/dashboard.fxml")
            );
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) botonAcceder.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SportApp - Panel de Administración");
            stage.show();

        } catch (Exception e) {
            AlertHelper.mostrarError("Error", "No se pudo cargar el dashboard: " + e.getMessage());
        }
    }
}