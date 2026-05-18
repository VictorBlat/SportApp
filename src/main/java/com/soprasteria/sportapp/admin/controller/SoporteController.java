package com.soprasteria.sportapp.admin.controller;

import com.soprasteria.sportapp.admin.model.SolicitudSoporte;
import com.soprasteria.sportapp.admin.service.AuthService;
import com.soprasteria.sportapp.admin.service.SoporteService;
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

public class SoporteController {

    @FXML private Label nombreAdminLabel;
    @FXML private Label pendientesLabel;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private VBox contenedorPrincipal;

    @FXML private TableView<SolicitudSoporte> tablaSolicitudes;
    @FXML private TableColumn<SolicitudSoporte, String> colUsuario;
    @FXML private TableColumn<SolicitudSoporte, String> colAsunto;
    @FXML private TableColumn<SolicitudSoporte, String> colEstado;
    @FXML private TableColumn<SolicitudSoporte, String> colFecha;

    // Panel detalle
    @FXML private Label detalleUsuario;
    @FXML private Label detalleAsunto;
    @FXML private Label detalleEstado;
    @FXML private Label detalleFecha;
    @FXML private TextArea detalleDescripcion;
    @FXML private Button btnEnRevision;
    @FXML private Button btnResuelto;

    private final ObservableList<SolicitudSoporte> listaSolicitudes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nombreAdminLabel.setText(SessionManager.getInstance().getAdminLogueado().getNombre());
        configurarFiltro();
        configurarTabla();
        cargarSolicitudes(null);
    }

    // ── Configuración ──────────────────────────────────────────────────────

    private void configurarFiltro() {
        filtroEstado.setItems(FXCollections.observableArrayList(
                "Todas", "pendiente", "en_revision", "resuelto"
        ));
        filtroEstado.getSelectionModel().selectFirst();
        filtroEstado.setOnAction(e -> {
            String sel = filtroEstado.getSelectionModel().getSelectedItem();
            String filtro = sel == null || sel.equals("Todas") ? null : "estado=eq." + sel;
            cargarSolicitudes(filtro);
        });
    }

    private void configurarTabla() {
        colUsuario.setCellValueFactory(c -> {
            String nombre = c.getValue().getUsuarioNombre();
            if (nombre == null || nombre.isBlank()) {
                String uid = c.getValue().getUsuarioId();
                nombre = uid != null && uid.length() >= 8 ? uid.substring(0, 8) + "..." : "-";
            }
            return new SimpleStringProperty(nombre);
        });

        colAsunto.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAsunto()));

        colEstado.setCellValueFactory(c -> {
            String estado = c.getValue().getEstado();
            String etiqueta = switch (estado) {
                case "pendiente"   -> "🕐 Pendiente";
                case "en_revision" -> "🔄 En revisión";
                case "resuelto"    -> "✅ Resuelto";
                default             -> estado;
            };
            return new SimpleStringProperty(etiqueta);
        });

        // Color por estado
        tablaSolicitudes.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(SolicitudSoporte s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) {
                    setStyle("");
                } else {
                    setStyle(switch (s.getEstado()) {
                        case "pendiente"   -> "-fx-background-color: #FEF9C3;";
                        case "en_revision" -> "-fx-background-color: #FED7AA;";
                        case "resuelto"    -> "-fx-background-color: #DCFCE7;";
                        default             -> "";
                    });
                }
            }
        });

        colFecha.setCellValueFactory(c -> {
            String fecha = c.getValue().getCreatedAt();
            if (fecha != null && fecha.length() >= 10) fecha = fecha.substring(0, 10);
            return new SimpleStringProperty(fecha != null ? fecha : "-");
        });

        tablaSolicitudes.setItems(listaSolicitudes);

        // Al seleccionar, mostrar detalle
        tablaSolicitudes.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) mostrarDetalle(sel);
            else limpiarDetalle();
        });
    }

    // ── Carga ──────────────────────────────────────────────────────────────

    private void cargarSolicitudes(String filtro) {
        SoporteService.obtenerSolicitudes(filtro)
                .thenAccept(solicitudes -> Platform.runLater(() -> {
                    listaSolicitudes.setAll(solicitudes);
                    limpiarDetalle();
                }))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            AlertHelper.mostrarError("Error", "No se pudieron cargar las solicitudes: " + e.getMessage()));
                    return null;
                });

        SoporteService.obtenerCountPendientes()
                .thenAccept(count -> Platform.runLater(() ->
                        pendientesLabel.setText(count + " pendientes")))
                .exceptionally(e -> null);
    }

    // ── Panel detalle ──────────────────────────────────────────────────────

    private void mostrarDetalle(SolicitudSoporte s) {
        String nombre = s.getUsuarioNombre();
        if (nombre == null || nombre.isBlank()) {
            String uid = s.getUsuarioId();
            nombre = uid != null && uid.length() >= 8 ? uid.substring(0, 8) + "..." : "-";
        }
        detalleUsuario.setText(nombre);
        detalleAsunto.setText(s.getAsunto());
        detalleEstado.setText(s.getEstado());
        detalleFecha.setText(s.getCreatedAt() != null && s.getCreatedAt().length() >= 10
                ? s.getCreatedAt().substring(0, 10) : "-");
        detalleDescripcion.setText(s.getDescripcion());

        btnEnRevision.setDisable(s.getEstado().equals("en_revision") || s.getEstado().equals("resuelto"));
        btnResuelto.setDisable(s.getEstado().equals("resuelto"));
    }

    private void limpiarDetalle() {
        detalleUsuario.setText("-");
        detalleAsunto.setText("-");
        detalleEstado.setText("-");
        detalleFecha.setText("-");
        detalleDescripcion.clear();
        btnEnRevision.setDisable(true);
        btnResuelto.setDisable(true);
    }

    // ── Acciones ───────────────────────────────────────────────────────────

    @FXML
    private void handleActualizar() {
        String sel = filtroEstado.getSelectionModel().getSelectedItem();
        String filtro = sel == null || sel.equals("Todas") ? null : "estado=eq." + sel;
        cargarSolicitudes(filtro);
    }

    @FXML
    private void handleMarcarEnRevision() {
        SolicitudSoporte sel = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        SoporteService.marcarEnRevision(sel.getId())
                .thenRun(() -> Platform.runLater(() -> {
                    AlertHelper.mostrarInfo("Éxito", "Solicitud marcada en revisión");
                    handleActualizar();
                }))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            AlertHelper.mostrarError("Error", "No se pudo actualizar: " + e.getMessage()));
                    return null;
                });
    }

    @FXML
    private void handleMarcarResuelto() {
        SolicitudSoporte sel = tablaSolicitudes.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        SoporteService.marcarResuelta(sel.getId())
                .thenRun(() -> Platform.runLater(() -> {
                    AlertHelper.mostrarInfo("Éxito", "Solicitud marcada como resuelta");
                    handleActualizar();
                }))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            AlertHelper.mostrarError("Error", "No se pudo actualizar: " + e.getMessage()));
                    return null;
                });
    }

    // ── Navegación ─────────────────────────────────────────────────────────

    @FXML private void navToDashboard()         { navToScene("/fxml/dashboard.fxml",          "Dashboard"); }
    @FXML private void navToUsuarios()          { navToScene("/fxml/usuarios.fxml",            "Gestión de Usuarios"); }
    @FXML private void navToEventos()           { navToScene("/fxml/eventos.fxml",             "Gestión de Eventos"); }
    @FXML private void navToEventosEspeciales() { navToScene("/fxml/eventos_especiales.fxml",  "Eventos Especiales"); }
    @FXML private void navToEstadisticas()      { navToScene("/fxml/estadisticas.fxml",        "Estadísticas"); }

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
