package com.soprasteria.sportapp.admin.controller;

import com.soprasteria.sportapp.admin.model.EventoEspecial;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EventosEspecialesController {

    @FXML private Label nombreAdminLabel;
    @FXML private Label contadorLabel;
    @FXML private VBox contenedorPrincipal;

    @FXML private TableView<EventoEspecial> tablaEventos;
    @FXML private TableColumn<EventoEspecial, String> colTitulo;
    @FXML private TableColumn<EventoEspecial, String> colTipo;
    @FXML private TableColumn<EventoEspecial, String> colFecha;
    @FXML private TableColumn<EventoEspecial, String> colHora;
    @FXML private TableColumn<EventoEspecial, String> colUbicacion;
    @FXML private TableColumn<EventoEspecial, String> colPlazas;
    @FXML private Button btnEliminar;

    @FXML private TextField tituloField;
    @FXML private ComboBox<String> tipoCombo;
    @FXML private TextArea descripcionArea;
    @FXML private DatePicker fechaPicker;
    @FXML private TextField horaField;
    @FXML private TextField maxParticipantesField;
    @FXML private TextField ubicacionField;

    private final ObservableList<EventoEspecial> listaEventos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nombreAdminLabel.setText(SessionManager.getInstance().getAdminLogueado().getNombre());
        configurarTabla();
        configurarFormulario();
        cargarEventos();
    }

    private void configurarTabla() {
        colTitulo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmoji() + " " + c.getValue().getTitulo()));

        colTipo.setCellValueFactory(c -> {
            String tipo = c.getValue().getTipo();
            String label = switch (tipo) {
                case "benefico"   -> "❤️ Benéfico";
                case "torneo"     -> "🏆 Torneo";
                case "exhibicion" -> "🎪 Exhibición";
                default            -> "⭐ Especial";
            };
            return new SimpleStringProperty(label);
        });

        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFecha()));

        colHora.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getHora()));

        colUbicacion.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getUbicacion()));

        colPlazas.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getParticipantes() + "/" + c.getValue().getMaxParticipantes()));

        tablaEventos.setItems(listaEventos);
        tablaEventos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                btnEliminar.setDisable(sel == null));
    }

    private void configurarFormulario() {
        tipoCombo.setItems(FXCollections.observableArrayList(
                "torneo", "benefico", "especial", "exhibicion"
        ));
        tipoCombo.getSelectionModel().selectFirst();
        maxParticipantesField.setText("100");
        fechaPicker.setValue(LocalDate.now().plusDays(7));
    }


    private void cargarEventos() {
        EventoService.obtenerEventosEspeciales(null)
                .thenAccept(eventos -> Platform.runLater(() -> {
                    listaEventos.setAll(eventos);
                    contadorLabel.setText(eventos.size() + " eventos");
                }))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            AlertHelper.mostrarError("Error", "No se pudieron cargar los eventos: " + e.getMessage()));
                    return null;
                });
    }


    @FXML
    private void handleCrear() {
        String titulo = tituloField.getText().trim();
        String tipo   = tipoCombo.getSelectionModel().getSelectedItem();

        if (titulo.isBlank()) {
            AlertHelper.mostrarError("Validación", "El título es obligatorio");
            return;
        }
        if (tipo == null) {
            AlertHelper.mostrarError("Validación", "Selecciona un tipo de evento");
            return;
        }

        String fecha = fechaPicker.getValue() != null ? fechaPicker.getValue().toString() : "";
        int maxParticipantes = 100;
        try {
            String maxStr = maxParticipantesField.getText().trim();
            if (!maxStr.isBlank()) maxParticipantes = Integer.parseInt(maxStr);
        } catch (NumberFormatException ignored) {}

        String adminId = SessionManager.getInstance().getAdminLogueado().getId();

        EventoEspecial nuevo = new EventoEspecial();
        nuevo.setTitulo(titulo);
        nuevo.setTipo(tipo);
        nuevo.setDescripcion(descripcionArea.getText().trim());
        nuevo.setFecha(fecha);
        nuevo.setHora(horaField.getText().trim());
        nuevo.setUbicacion(ubicacionField.getText().trim());
        nuevo.setMaxParticipantes(maxParticipantes);
        nuevo.setParticipantes(0);
        nuevo.setAdminCreadorId(adminId);
        nuevo.setEmoji(EventoEspecial.getEmojiPorTipo(tipo));

        EventoService.crearEventoEspecial(nuevo)
                .thenAccept(creado -> Platform.runLater(() -> {
                    AlertHelper.mostrarInfo("Éxito", "Evento especial creado correctamente");
                    handleLimpiar();
                    cargarEventos();
                }))
                .exceptionally(e -> {
                    Platform.runLater(() ->
                            AlertHelper.mostrarError("Error", "No se pudo crear el evento: " + e.getMessage()));
                    return null;
                });
    }

    @FXML
    private void handleEliminar() {
        EventoEspecial evento = tablaEventos.getSelectionModel().getSelectedItem();
        if (evento == null) return;

        boolean confirmar = AlertHelper.mostrarConfirmacion(
                "Eliminar evento especial",
                "¿Eliminar el evento \"" + evento.getTitulo() + "\"? Esta acción no se puede deshacer."
        );

        if (confirmar) {
            EventoService.eliminarEventoEspecial(evento.getId())
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

    @FXML
    private void handleLimpiar() {
        tituloField.clear();
        descripcionArea.clear();
        horaField.clear();
        ubicacionField.clear();
        maxParticipantesField.setText("100");
        fechaPicker.setValue(LocalDate.now().plusDays(7));
        tipoCombo.getSelectionModel().selectFirst();
    }


    @FXML private void navToDashboard()  { navToScene("/fxml/dashboard.fxml",         "Dashboard"); }
    @FXML private void navToUsuarios()   { navToScene("/fxml/usuarios.fxml",           "Gestión de Usuarios"); }
    @FXML private void navToEventos()    { navToScene("/fxml/eventos.fxml",            "Gestión de Eventos"); }
    @FXML private void navToSoporte()    { navToScene("/fxml/soporte.fxml",            "Soporte"); }
    @FXML private void navToEstadisticas(){ navToScene("/fxml/estadisticas.fxml",      "Estadísticas"); }

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
