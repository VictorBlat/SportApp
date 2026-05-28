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
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml")
        );
        Scene scene = new Scene(loader.load(), 1200, 800);

        primaryStage.setTitle("SportApp - Panel de Administración");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setOnCloseRequest(e -> {
            SessionManager.getInstance().cerrarSesion();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}