package com.videogames.library.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AuthentificationController {

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
        String email = loginEmail.getText();
        String mdp = loginMdp.getText();
        
        loginMessageErreur.setText("TODO: AuthService.seConnecter(" + email + ")");
    }

    @FXML
    private void surInscription() {
        String email = registerEmail.getText();
        String pseudo = registerPseudo.getText();
        String mdp = registerMdp.getText();
        
        registerMessageErreur.setText("TODO: AuthService.inscrire(" + pseudo + ")");
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
}