package com.soprasteria.sportapp.admin.controller;

import com.soprasteria.sportapp.admin.model.EventoActividad;
import com.soprasteria.sportapp.admin.service.AuthService;
import com.soprasteria.sportapp.admin.service.EventoService;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class EventosController {

    @FXML private Label nombreAdminLabel;
    @FXML private Label contadorLabel;
    @FXML private TextField buscadorField;
    @FXML private ComboBox<String> filtroDeporte;
    @FXML private VBox contenedorPrincipal;

    @FXML private TableView<EventoActividad> tablaEventos;
    @FXML private TableColumn<EventoActividad, String> colTitulo;
    @FXML private TableColumn<EventoActividad, String> colDeporte;
    @FXML private TableColumn<EventoActividad, String> colFecha;
    @FXML private TableColumn<EventoActividad, String> colHora;
    @FXML private TableColumn<EventoActividad, String> colCreador;
    @FXML private TableColumn<EventoActividad, String> colParticipantes;

    @FXML private Button btnEliminar;

    private final ObservableList<EventoActividad> listaEventos = FXCollections.observableArrayList();
    private List<EventoActividad> todosLosEventos;

    @FXML
    public void initialize() {
        nombreAdminLabel.setText(SessionManager.getInstance().getAdminLogueado().getNombre());
        configurarTabla();
        configurarSeleccion();
        cargarEventos();

        filtroDeporte.setOnAction(e -> aplicarFiltroDeporte());
    }

    // ── Tabla ──────────────────────────────────────────────────────────────

    private void configurarTabla() {
        colTitulo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmoji() + " " + c.getValue().getTitulo()));

        colDeporte.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDeporte()));

        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFecha()));

        colHora.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getHora()));

        colCreador.setCellValueFactory(c -> {
            String creador = c.getValue().getCreadorNombre();
            if (creador == null || creador.isBlank()) {
                creador = c.getValue().getCreadorId().substring(0, 8) + "...";
            }
            return new SimpleStringProperty(creador);
        });

        // Participantes con barra de progreso
        colParticipantes.setCellFactory(col -> new TableCell<>() {
            private final ProgressBar pb = new ProgressBar(0);
            private final Label lbl = new Label();
            private final HBox box = new HBox(6, pb, lbl);

            {
                pb.setPrefWidth(70);
                box.setStyle("-fx-alignment: CENTER_LEFT;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    EventoActividad ev = (EventoActividad) getTableRow().getItem();
                    double ratio = ev.getMaxParticipantes() > 0
                            ? (double) ev.getParticipantes() / ev.getMaxParticipantes() : 0;
                    pb.setProgress(ratio);
                    lbl.setText(ev.getParticipantes() + "/" + ev.getMaxParticipantes());
                    setGraphic(box);
                }
            }
        });

        tablaEventos.setItems(listaEventos);
    }

    private void configurarSeleccion() {
        tablaEventos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                btnEliminar.setDisable(sel == null));
    }

    // ── Carga ──────────────────────────────────────────────────────────────

    private void cargarEventos() {
        EventoService.obtenerEventos(null)
                .thenAccept(eventos -> Platform.runLater(() -> {
                    todosLosEventos = eventos;
                    listaEventos.setAll(eventos);
                    contadorLabel.setText(eventos.size() + " eventos");
                    actualizarFiltroDeporte(eventos);
                }))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            AlertHelper.mostrarError("Error", "No se pudieron cargar los eventos: " + e.getMessage()));
                    return null;
                });
    }

    private void actualizarFiltroDeporte(List<EventoActividad> eventos) {
        List<String> deportes = eventos.stream()
                .map(EventoActividad::getDeporte)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        deportes.add(0, "Todos los deportes");
        filtroDeporte.setItems(FXCollections.observableArrayList(deportes));
        filtroDeporte.getSelectionModel().selectFirst();
    }

    // ── Acciones ───────────────────────────────────────────────────────────

    @FXML
    private void handleBuscar() {
        String termino = buscadorField.getText().trim();
        if (termino.isBlank()) {
            cargarEventos();
            return;
        }

        EventoService.buscarEventos(termino)
                .thenAccept(eventos -> Platform.runLater(() -> {
                    listaEventos.setAll(eventos);
                    contadorLabel.setText(eventos.size() + " resultados");
                }))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            AlertHelper.mostrarError("Error", "Error en la búsqueda: " + e.getMessage()));
                    return null;
                });
    }

    @FXML
    private void handleMostrarTodos() {
        buscadorField.clear();
        cargarEventos();
    }

    private void aplicarFiltroDeporte() {
        String seleccionado = filtroDeporte.getSelectionModel().getSelectedItem();
        if (seleccionado == null || seleccionado.equals("Todos los deportes")) {
            listaEventos.setAll(todosLosEventos);
            contadorLabel.setText(todosLosEventos.size() + " eventos");
            return;
        }

        List<EventoActividad> filtrados = todosLosEventos.stream()
                .filter(e -> e.getDeporte().equalsIgnoreCase(seleccionado))
                .collect(Collectors.toList());
        listaEventos.setAll(filtrados);
        contadorLabel.setText(filtrados.size() + " eventos");
    }

    @FXML
    private void handleEliminar() {
        EventoActividad evento = tablaEventos.getSelectionModel().getSelectedItem();
        if (evento == null) return;

        boolean confirmar = AlertHelper.mostrarConfirmacion(
                "Eliminar evento",
                "¿Estás seguro de que quieres eliminar el evento \"" + evento.getTitulo() + "\"?\nEsta acción no se puede deshacer."
        );

        if (confirmar) {
            EventoService.eliminarEvento(evento.getId())
                    .thenRun(() -> Platform.runLater(() -> {
                        AlertHelper.mostrarInfo("Éxito", "Evento eliminado correctamente");
                        cargarEventos();
                    }))
                    .exceptionally(e -> {
                        Platform.runLater(() ->
                                AlertHelper.mostrarError("Error", "No se pudo eliminar el evento: " + e.getMessage()));
                        return null;
                    });
        }
    }

    // ── Navegación ─────────────────────────────────────────────────────────

    @FXML private void navToDashboard()         { navToScene("/fxml/dashboard.fxml",          "Dashboard"); }
    @FXML private void navToUsuarios()          { navToScene("/fxml/usuarios.fxml",            "Gestión de Usuarios"); }
    @FXML private void navToEventosEspeciales() { navToScene("/fxml/eventos_especiales.fxml",  "Eventos Especiales"); }
    @FXML private void navToSoporte()           { navToScene("/fxml/soporte.fxml",             "Soporte"); }
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
