package com.videogames.library;

import com.videogames.library.util.DataInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        initialiserBase();

        Parent root = FXMLLoader.load(
                getClass().getResource("/com/videogames/library/authentification.fxml"));

        Scene scene = new Scene(root, 960, 640);
        scene.getStylesheets().add(
                getClass().getResource("/com/videogames/library/styles.css").toExternalForm());

        stage.setTitle("GameLib - Connexion");
        stage.setScene(scene);
        stage.show();
    }

    private void initialiserBase() {
        try {
            DataInitializer.initialiser();
        } catch (Throwable t) {
            System.err.println("[App] Base de donnees indisponible : " + t.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
