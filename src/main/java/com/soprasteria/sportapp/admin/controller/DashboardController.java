package com.soprasteria.sportapp.admin.controller;

import com.soprasteria.sportapp.admin.model.Administrador;
import com.soprasteria.sportapp.admin.service.AuthService;
import com.soprasteria.sportapp.admin.service.EventoService;
import com.soprasteria.sportapp.admin.service.SoporteService;
import com.soprasteria.sportapp.admin.service.UsuarioService;
import com.soprasteria.sportapp.admin.util.AlertHelper;
import com.soprasteria.sportapp.admin.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador del dashboard principal.
 * Muestra estadísticas y menú de navegación.
 */
public class DashboardController {

    @FXML
    private Label nombreAdminLabel;

    @FXML
    private VBox menuLateral;

    @FXML
    private VBox contenedorPrincipal;

    @FXML
    private Label totalUsuariosLabel;

    @FXML
    private Label totalEventosLabel;

    @FXML
    private Label solicitudesPendientesLabel;

    @FXML
    private Label ingresosLabel;

    @FXML
    private BarChart<String, Number> graficosEventos;

    @FXML
    private Button botonCerrarSesion;

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().tieneSesion()) {
            navToLogin();
            return;
        }

        Administrador admin = SessionManager.getInstance().getAdminLogueado();
        nombreAdminLabel.setText(admin.getNombre());

        cargarEstadisticas();
        cargarGraficos();
    }

    /**
     * Carga las estadísticas principales.
     */
    private void cargarEstadisticas() {
        UsuarioService.obtenerUsuarios(null)
                .thenAccept(usuarios -> Platform.runLater(() -> {
                    totalUsuariosLabel.setText(String.valueOf(usuarios.size()));
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> totalUsuariosLabel.setText("Error"));
                    return null;
                });

        EventoService.obtenerEventos(null)
                .thenAccept(eventos -> Platform.runLater(() -> {
                    totalEventosLabel.setText(String.valueOf(eventos.size()));
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> totalEventosLabel.setText("Error"));
                    return null;
                });

        SoporteService.obtenerSolicitudes("estado=eq.pendiente")
                .thenAccept(solicitudes -> Platform.runLater(() -> {
                    solicitudesPendientesLabel.setText(String.valueOf(solicitudes.size()));
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> solicitudesPendientesLabel.setText("Error"));
                    return null;
                });
    }

    /**
     * Carga los gráficos de estadísticas.
     */
    private void cargarGraficos() {
        EventoService.obtenerEventos(null)
                .thenAccept(eventos -> Platform.runLater(() -> {
                    java.util.Map<String, Integer> eventosPorDeporte = new java.util.HashMap<>();

                    eventos.forEach(evento -> {
                        String deporte = evento.getDeporte();
                        eventosPorDeporte.put(
                                deporte,
                                eventosPorDeporte.getOrDefault(deporte, 0) + 1
                        );
                    });

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Eventos por deporte");

                    eventosPorDeporte.forEach((deporte, cantidad) ->
                            series.getData().add(new XYChart.Data<>(deporte, cantidad))
                    );

                    graficosEventos.getData().clear();
                    graficosEventos.getData().add(series);
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> AlertHelper.mostrarError("Error", "No se pudieron cargar los gráficos"));
                    return null;
                });
    }

    /**
     * Navega a la sección de usuarios.
     */
    @FXML
    private void navToUsuarios() {
        navToScene("/fxml/usuarios.fxml", "Gestión de Usuarios");
    }

    /**
     * Navega a la sección de eventos.
     */
    @FXML
    private void navToEventos() {
        navToScene("/fxml/eventos.fxml", "Gestión de Eventos");
    }

    /**
     * Navega a la sección de eventos especiales.
     */
    @FXML
    private void navToEventosEspeciales() {
        navToScene("/fxml/eventos_especiales.fxml", "Eventos Especiales");
    }

    /**
     * Navega a la sección de soporte.
     */
    @FXML
    private void navToSoporte() {
        navToScene("/fxml/soporte.fxml", "Solicitudes de Soporte");
    }

    /**
     * Navega a la sección de estadísticas.
     */
    @FXML
    private void navToEstadisticas() {
        navToScene("/fxml/estadisticas.fxml", "Estadísticas");
    }

    /**
     * Navega a una nueva escena.
     */
    private void navToScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) contenedorPrincipal.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SportApp - " + title);
            stage.show();
        } catch (Exception e) {
            AlertHelper.mostrarError("Error", "No se pudo cargar la pantalla: " + e.getMessage());
        }
    }

    /**
     * Maneja el cierre de sesión.
     */
    @FXML
    private void handleCerrarSesion() {
        boolean confirmar = AlertHelper.mostrarConfirmacion(
                "Cerrar sesión",
                "¿Estás seguro de que deseas cerrar sesión?"
        );

        if (confirmar) {
            AuthService.logout();
            navToLogin();
        }
    }

    /**
     * Navega a la pantalla de login.
     */
    private void navToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml")
            );
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) contenedorPrincipal.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SportApp - Login");
            stage.show();
        } catch (Exception e) {
            AlertHelper.mostrarError("Error", "No se pudo cargar el login: " + e.getMessage());
        }
    }
}