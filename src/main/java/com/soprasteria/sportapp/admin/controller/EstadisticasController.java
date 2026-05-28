package com.soprasteria.sportapp.admin.controller;

import com.soprasteria.sportapp.admin.service.AuthService;
import com.soprasteria.sportapp.admin.service.EventoService;
import com.soprasteria.sportapp.admin.service.SupabaseService;
import com.soprasteria.sportapp.admin.service.UsuarioService;
import com.soprasteria.sportapp.admin.util.AlertHelper;
import com.soprasteria.sportapp.admin.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EstadisticasController {

    @FXML private Label nombreAdminLabel;
    @FXML private VBox contenedorPrincipal;

    @FXML private Label cardUsuarios;
    @FXML private Label cardEventos;
    @FXML private Label cardEventosActivos;
    @FXML private Label cardMensajes;
    @FXML private Label cardIngresos;

    @FXML private BarChart<String, Number>  barEventosDeporte;
    @FXML private PieChart                  pieDeportes;
    @FXML private LineChart<String, Number> lineUsuariosMes;

    @FXML
    public void initialize() {
        nombreAdminLabel.setText(SessionManager.getInstance().getAdminLogueado().getNombre());
        cargarTodo();
    }

    private void cargarTodo() {
        cargarUsuarios();
        cargarEventos();
        cargarMensajes();
        cargarIngresos();
        cargarDeportesUsuarios();
    }

    private void cargarUsuarios() {
        UsuarioService.obtenerUsuarios(null)
                .thenAccept(usuarios -> Platform.runLater(() -> {
                    cardUsuarios.setText(String.valueOf(usuarios.size()));

                    Map<String, Integer> porMes = new TreeMap<>();
                    usuarios.forEach(u -> {
                        String fecha = u.getCreatedAt();
                        if (fecha != null && fecha.length() >= 7) {
                            String mes = fecha.substring(0, 7);
                            porMes.merge(mes, 1, Integer::sum);
                        }
                    });

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Usuarios");
                    porMes.forEach((mes, count) -> series.getData().add(new XYChart.Data<>(mes, count)));

                    lineUsuariosMes.getData().clear();
                    lineUsuariosMes.getData().add(series);
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> cardUsuarios.setText("Error"));
                    return null;
                });
    }

    private void cargarEventos() {
        EventoService.obtenerEventos(null)
                .thenAccept(eventos -> Platform.runLater(() -> {
                    cardEventos.setText(String.valueOf(eventos.size()));

                    String hoy = LocalDate.now().toString();
                    long activos = eventos.stream()
                            .filter(e -> e.getFecha() != null && e.getFecha().compareTo(hoy) >= 0)
                            .count();
                    cardEventosActivos.setText(String.valueOf(activos));

                    Map<String, Integer> porDeporte = new HashMap<>();
                    eventos.forEach(e -> porDeporte.merge(e.getDeporte(), 1, Integer::sum));

                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Eventos");
                    porDeporte.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                            .forEach(entry -> series.getData().add(
                                    new XYChart.Data<>(entry.getKey(), entry.getValue())));

                    barEventosDeporte.getData().clear();
                    barEventosDeporte.getData().add(series);
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        cardEventos.setText("Error");
                        cardEventosActivos.setText("Error");
                    });
                    return null;
                });
    }

    private void cargarMensajes() {
        SupabaseService.countFromTableAdmin("mensaje")
                .thenAccept(result -> Platform.runLater(() -> {
                    if (result != null && result.size() > 0) {
                        int count = result.get(0).getAsJsonObject().get("count").getAsInt();
                        cardMensajes.setText(String.valueOf(count));
                    } else {
                        cardMensajes.setText("0");
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> cardMensajes.setText("—"));
                    return null;
                });
    }

    private void cargarIngresos() {
        SupabaseService.getFromTableAdmin("transaccion_premium", "select=monto")
                .thenAccept(result -> Platform.runLater(() -> {
                    if (result == null || result.isEmpty()) {
                        cardIngresos.setText("€0.00");
                        return;
                    }
                    double total = 0;
                    for (int i = 0; i < result.size(); i++) {
                        var obj = result.get(i).getAsJsonObject();
                        if (obj.has("monto") && !obj.get("monto").isJsonNull()) {
                            total += obj.get("monto").getAsDouble();
                        }
                    }
                    cardIngresos.setText(String.format("€%.2f", total));
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> cardIngresos.setText("—"));
                    return null;
                });
    }

    private void cargarDeportesUsuarios() {
        SupabaseService.getFromTableAdmin("perfil_deportivo", "select=deporte")
                .thenAccept(result -> Platform.runLater(() -> {
                    if (result == null || result.isEmpty()) return;

                    Map<String, Integer> porDeporte = new HashMap<>();
                    result.forEach(item -> {
                        var obj = item.getAsJsonObject();
                        if (obj.has("deporte") && !obj.get("deporte").isJsonNull()) {
                            porDeporte.merge(obj.get("deporte").getAsString(), 1, Integer::sum);
                        }
                    });

                    pieDeportes.getData().clear();
                    porDeporte.forEach((deporte, count) ->
                            pieDeportes.getData().add(new PieChart.Data(deporte + " (" + count + ")", count)));
                }))
                .exceptionally(e -> null);
    }


    @FXML private void navToDashboard()         { navToScene("/fxml/dashboard.fxml",         "Dashboard"); }
    @FXML private void navToUsuarios()          { navToScene("/fxml/usuarios.fxml",           "Gestión de Usuarios"); }
    @FXML private void navToEventos()           { navToScene("/fxml/eventos.fxml",            "Gestión de Eventos"); }
    @FXML private void navToEventosEspeciales() { navToScene("/fxml/eventos_especiales.fxml", "Eventos Especiales"); }
    @FXML private void navToSoporte()           { navToScene("/fxml/soporte.fxml",            "Soporte"); }

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

    @FXML
    private void handleCerrarSesion() {
        if (AlertHelper.mostrarConfirmacion("Cerrar sesión", "¿Estás seguro de que deseas cerrar sesión?")) {
            AuthService.logout();
            navToScene("/fxml/login.fxml", "Login");
        }
    }
}