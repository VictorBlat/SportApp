package com.soprasteria.sportapp.admin;

import com.soprasteria.sportapp.admin.util.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicación JavaFX.
 * Carga la pantalla de login al iniciar.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar login.fxml
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );
        Scene scene = new Scene(loader.load(), 900, 600);

        primaryStage.setTitle("SportApp - Panel de Administración");
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);
        primaryStage.setOnCloseRequest(e -> {
            // Limpiar sesión al cerrar la app
            SessionManager.getInstance().cerrarSesion();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}