package com.videogames.library.controller;

import com.videogames.library.model.Utilisateur;
import com.videogames.library.service.AuthService;
import com.videogames.library.util.Navigation;
import com.videogames.library.util.SessionUtilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AuthentificationController {

    private final AuthService authService = new AuthService();

    @FXML private VBox loginBox;
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginMdp;
    @FXML private Label loginMessageErreur;

    @FXML private VBox registerBox;
    @FXML private TextField registerEmail;
    @FXML private TextField registerPseudo;
    @FXML private PasswordField registerMdp;
    @FXML private Label registerMessageErreur;

    @FXML
    private void initialize() {
    }

    @FXML
    private void surConnexion() {
        loginMessageErreur.setText("");
        try {
            Utilisateur utilisateur = authService.connecter(loginEmail.getText(), loginMdp.getText());
            if (utilisateur == null) {
                loginMessageErreur.setText("Email ou mot de passe incorrect.");
                return;
            }
            SessionUtilisateur.ouvrir(utilisateur);
            Navigation.accueil(stage());
        } catch (Throwable e) {
            loginMessageErreur.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void surInscription() {
        registerMessageErreur.setText("");
        try {
            Utilisateur utilisateur = authService.inscrire(
                    registerEmail.getText(), registerPseudo.getText(), registerMdp.getText());
            SessionUtilisateur.ouvrir(utilisateur);
            Navigation.accueil(stage());
        } catch (IllegalArgumentException e) {
            registerMessageErreur.setText(e.getMessage());
        } catch (Throwable e) {
            registerMessageErreur.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void basculerVersInscription() {
        loginBox.setVisible(false);
        loginBox.setManaged(false);

        registerBox.setVisible(true);
        registerBox.setManaged(true);
    }

    @FXML
    private void basculerVersConnexion() {
        registerBox.setVisible(false);
        registerBox.setManaged(false);

        loginBox.setVisible(true);
        loginBox.setManaged(true);
    }

    private Stage stage() {
        return (Stage) loginEmail.getScene().getWindow();
    }
}
