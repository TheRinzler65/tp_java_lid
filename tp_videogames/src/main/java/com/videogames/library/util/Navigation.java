package com.videogames.library.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Changement d'écran centralisé (connexion / accueil).
 */
public final class Navigation {

    private static final String BASE = "/com/videogames/library/";
    private static final String CSS = "styles.css";

    private Navigation() {
    }

    public static void authentification(Stage stage) {
        charger(stage, "authentification.fxml", "GameLib - Connexion", 960, 640);
    }

    public static void accueil(Stage stage) {
        charger(stage, "accueil.fxml", "GameLib", 1180, 760);
    }

    private static void charger(Stage stage, String fichier, String titre, int largeur, int hauteur) {
        try {
            URL url = Navigation.class.getResource(BASE + fichier);
            if (url == null) {
                throw new IllegalStateException("FXML introuvable : " + BASE + fichier);
            }
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root, largeur, hauteur);
            URL css = Navigation.class.getResource(BASE + CSS);
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setTitle(titre);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger l'ecran " + fichier, e);
        }
    }
}
