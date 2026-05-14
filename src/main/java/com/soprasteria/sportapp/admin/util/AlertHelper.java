package com.soprasteria.sportapp.admin.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Clase auxiliar para mostrar alertas y diálogos reutilizables.
 */
public class AlertHelper {

    /**
     * Muestra una alerta de información.
     */
    public static void mostrarInfo(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        aplicarEstilo(alert);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de error.
     */
    public static void mostrarError(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        aplicarEstilo(alert);
        alert.showAndWait();
    }

    /**
     * Muestra una alerta de advertencia.
     */
    public static void mostrarAdvertencia(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        aplicarEstilo(alert);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de confirmación (Sí/No).
     *
     * @return true si el usuario selecciona "Aceptar", false si cancela
     */
    public static boolean mostrarConfirmacion(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        aplicarEstilo(alert);
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    /**
     * Muestra un diálogo de entrada de texto.
     *
     * @param titulo       Título del diálogo
     * @param etiqueta     Etiqueta del campo de entrada
     * @param textoDefault Texto inicial en el campo
     * @return El texto ingresado o null si se cancela
     */
    public static String mostrarInputDialog(String titulo, String etiqueta, String textoDefault) {
        TextInputDialog dialog = new TextInputDialog(textoDefault != null ? textoDefault : "");
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(etiqueta);
        aplicarEstilo(dialog.getDialogPane());
        Optional<String> resultado = dialog.showAndWait();
        return resultado.orElse(null);
    }

    /**
     * Aplica el estilo personalizado a un Alert.
     */
    private static void aplicarEstilo(Alert alert) {
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        aplicarEstilo(alert.getDialogPane());
    }

    /**
     * Aplica el estilo personalizado a un DialogPane.
     */
    private static void aplicarEstilo(javafx.scene.control.DialogPane dialogPane) {
        String css = AlertHelper.class.getResource("/styles/style.css").toExternalForm();
        dialogPane.getStylesheets().add(css);
    }
}