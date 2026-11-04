package com.videogames.library.controller;

import com.videogames.library.model.Utilisateur;
import com.videogames.library.util.Navigation;
import com.videogames.library.util.SessionUtilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AccueilController {

    @FXML private Label pseudoLabel;

    @FXML
    private void initialize() {
        Utilisateur utilisateur = SessionUtilisateur.get();
        pseudoLabel.setText(utilisateur == null ? "" : "Connecté : " + utilisateur.getPseudo());
    }

    @FXML
    private void seDeconnecter() {
        SessionUtilisateur.fermer();
        Navigation.authentification((Stage) pseudoLabel.getScene().getWindow());
    }
}
