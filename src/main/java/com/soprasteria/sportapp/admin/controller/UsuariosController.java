package com.soprasteria.sportapp.admin.controller;

import com.soprasteria.sportapp.admin.model.Usuario;
import com.soprasteria.sportapp.admin.model.UsuarioBaneado;
import com.soprasteria.sportapp.admin.service.AuthService;
import com.soprasteria.sportapp.admin.service.UsuarioService;
import com.soprasteria.sportapp.admin.util.AlertHelper;
import com.soprasteria.sportapp.admin.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

/**
 * Controlador de la pantalla de gestión de usuarios.
 */
public class UsuariosController {

    @FXML private Label nombreAdminLabel;
    @FXML private Label contadorLabel;
    @FXML private TextField buscadorField;
    @FXML private VBox contenedorPrincipal;

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colUbicacion;
    @FXML private TableColumn<Usuario, String> colFecha;
    @FXML private TableColumn<Usuario, String> colEstado;

    @FXML private Button btnVerDetalle;
    @FXML private Button btnBanear;
    @FXML private Button btnDesbanear;

    private final Map<String, UsuarioBaneado> baneosActivos = new HashMap<>();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private final java.util.List<Usuario> todosLosUsuarios = new ArrayList<>();

    @FXML
    public void initialize() {
        nombreAdminLabel.setText(SessionManager.getInstance().getAdminLogueado().getNombre());

        configurarTabla();
        configurarSeleccion();
        cargarUsuarios();

        buscadorField.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo.isBlank()) {
                cargarUsuarios();
            }
        });
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNombre()));

        colUbicacion.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getUbicacion() != null ? c.getValue().getUbicacion() : "-"));

        colFecha.setCellValueFactory(c -> {
            String fecha = c.getValue().getCreatedAt();
            if (fecha != null && fecha.length() >= 10) fecha = fecha.substring(0, 10);
            return new SimpleStringProperty(fecha != null ? fecha : "-");
        });

        colEstado.setCellValueFactory(c -> {
            boolean baneado = baneosActivos.containsKey(c.getValue().getId());
            return new SimpleStringProperty(baneado ? "🚫 Baneado" : "✅ Activo");
        });

        tablaUsuarios.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Usuario usuario, boolean empty) {
                super.updateItem(usuario, empty);
                if (empty || usuario == null) {
                    setStyle("");
                } else if (baneosActivos.containsKey(usuario.getId())) {
                    setStyle("-fx-background-color: #FEE2E2;");
                } else {
                    setStyle("");
                }
            }
        });

        tablaUsuarios.setItems(listaUsuarios);
    }

    private void configurarSeleccion() {
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, seleccionado) -> {
            boolean haySeleccion = seleccionado != null;
            btnVerDetalle.setDisable(!haySeleccion);

            if (haySeleccion) {
                boolean esBaneado = baneosActivos.containsKey(seleccionado.getId());
                btnBanear.setDisable(esBaneado);
                btnDesbanear.setDisable(!esBaneado);
            } else {
                btnBanear.setDisable(true);
                btnDesbanear.setDisable(true);
            }
        });
    }


    private void cargarUsuarios() {
        UsuarioService.obtenerUsuarios(null)
                .thenAccept(usuarios -> {
                    cargarBaneosYActualizar(usuarios);
                })
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            AlertHelper.mostrarError("Error", "No se pudieron cargar los usuarios: " + e.getMessage()));
                    return null;
                });
    }

    private void cargarBaneosYActualizar(List<Usuario> usuarios) {
        UsuarioService.obtenerTodosBaneos().thenAccept(baneos -> {
            baneosActivos.clear();
            baneos.forEach(b -> baneosActivos.put(b.getUsuarioId(), b));

            Platform.runLater(() -> {
                todosLosUsuarios.clear();
                todosLosUsuarios.addAll(usuarios);
                listaUsuarios.setAll(usuarios);
                contadorLabel.setText(usuarios.size() + " usuarios");
                tablaUsuarios.refresh();
            });
        }).exceptionally(e -> {
            Platform.runLater(() -> {
                todosLosUsuarios.clear();
                todosLosUsuarios.addAll(usuarios);
                listaUsuarios.setAll(usuarios);
                contadorLabel.setText(usuarios.size() + " usuarios");
            });
            return null;
        });
    }


    @FXML
    private void handleBuscar() {
        String termino = buscadorField.getText().trim();
        if (termino.isBlank()) {
            cargarUsuarios();
            return;
        }

        String terminoLower = termino.toLowerCase();
        java.util.List<Usuario> filtrados = todosLosUsuarios.stream()
                .filter(u -> u.getNombre() != null &&
                        u.getNombre().toLowerCase().contains(terminoLower))
                .collect(java.util.stream.Collectors.toList());
        listaUsuarios.setAll(filtrados);
        contadorLabel.setText(filtrados.size() + " resultados");
        tablaUsuarios.refresh();
    }

    @FXML
    private void handleMostrarTodos() {
        buscadorField.clear();
        cargarUsuarios();
    }

    @FXML
    private void handleVerDetalle() {
        Usuario usuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario == null) return;

        boolean baneado = baneosActivos.containsKey(usuario.getId());
        UsuarioBaneado baneo = baneosActivos.get(usuario.getId());

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(usuario.getId()).append("\n");
        sb.append("Nombre: ").append(usuario.getNombre()).append("\n");
        sb.append("Ubicación: ").append(usuario.getUbicacion() != null ? usuario.getUbicacion() : "-").append("\n");
        sb.append("Registro: ").append(
                usuario.getCreatedAt() != null && usuario.getCreatedAt().length() >= 10
                        ? usuario.getCreatedAt().substring(0, 10) : "-").append("\n");
        sb.append("Estado: ").append(baneado ? "🚫 BANEADO" : "✅ Activo").append("\n");

        if (baneado && baneo != null) {
            sb.append("\nMotivo del baneo:\n").append(baneo.getMotivo());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle del usuario");
        alert.setHeaderText(usuario.getNombre());
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    @FXML
    private void handleBanear() {
        Usuario usuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Banear usuario");
        dialog.setHeaderText("Banear a: " + usuario.getNombre());
        dialog.setContentText("Motivo del baneo:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(motivo -> {
            if (motivo.isBlank()) {
                AlertHelper.mostrarError("Error", "El motivo no puede estar vacío");
                return;
            }

            String adminId = SessionManager.getInstance().getAdminLogueado().getId();
            UsuarioService.banearUsuario(usuario.getId(), motivo, adminId)
                    .thenRun(() -> Platform.runLater(() -> {
                        AlertHelper.mostrarInfo("Éxito", "Usuario baneado correctamente");
                        cargarUsuarios();
                    }))
                    .exceptionally(e -> {
                        Platform.runLater(() ->
                                AlertHelper.mostrarError("Error", "No se pudo banear al usuario: " + e.getMessage()));
                        return null;
                    });
        });
    }

    @FXML
    private void handleDesbanear() {
        Usuario usuario = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuario == null) return;

        UsuarioBaneado baneo = baneosActivos.get(usuario.getId());
        if (baneo == null) return;

        boolean confirmar = AlertHelper.mostrarConfirmacion(
                "Desbanear usuario",
                "¿Estás seguro de que quieres desbanear a " + usuario.getNombre() + "?"
        );

        if (confirmar) {
            UsuarioService.desbanearUsuario(baneo.getId())
                    .thenRun(() -> Platform.runLater(() -> {
                        AlertHelper.mostrarInfo("Éxito", "Usuario desbaneado correctamente");
                        cargarUsuarios();
                    }))
                    .exceptionally(e -> {
                        Platform.runLater(() ->
                                AlertHelper.mostrarError("Error", "No se pudo desbanear al usuario: " + e.getMessage()));
                        return null;
                    });
        }
    }


    @FXML private void navToDashboard()        { navToScene("/fxml/dashboard.fxml",         "Dashboard"); }
    @FXML private void navToEventos()          { navToScene("/fxml/eventos.fxml",            "Gestión de Eventos"); }
    @FXML private void navToEventosEspeciales(){ navToScene("/fxml/eventos_especiales.fxml", "Eventos Especiales"); }
    @FXML private void navToSoporte()          { navToScene("/fxml/soporte.fxml",            "Soporte"); }
    @FXML private void navToEstadisticas()     { navToScene("/fxml/estadisticas.fxml",       "Estadísticas"); }

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
        boolean confirmar = AlertHelper.mostrarConfirmacion(
                "Cerrar sesión", "¿Estás seguro de que deseas cerrar sesión?");
        if (confirmar) {
            AuthService.logout();
            navToScene("/fxml/login.fxml", "Login");
        }
    }
}