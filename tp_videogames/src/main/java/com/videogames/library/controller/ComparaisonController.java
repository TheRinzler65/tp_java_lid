package com.videogames.library.controller;

import com.videogames.library.model.Jeu;
import com.videogames.library.model.Utilisateur;
import com.videogames.library.service.AmiService;
import com.videogames.library.service.ComparaisonResultat;
import com.videogames.library.service.ComparaisonService;
import com.videogames.library.util.SessionUtilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ComparaisonController {

    private final AmiService amiService = new AmiService();
    private final ComparaisonService comparaisonService = new ComparaisonService();

    private final ObservableList<Jeu> communs = FXCollections.observableArrayList();
    private final ObservableList<Jeu> seulementMoi = FXCollections.observableArrayList();
    private final ObservableList<Jeu> seulementAutre = FXCollections.observableArrayList();

    @FXML private ComboBox<Utilisateur> amiCombo;
    @FXML private Label message;
    @FXML private Label titreSeulementAutre;
    @FXML private ListView<Jeu> communsList;
    @FXML private ListView<Jeu> seulementMoiList;
    @FXML private ListView<Jeu> seulementAutreList;

    @FXML
    private void initialize() {
        communsList.setItems(communs);
        seulementMoiList.setItems(seulementMoi);
        seulementAutreList.setItems(seulementAutre);
        rafraichirAmis();
    }

    private void rafraichirAmis() {
        amiCombo.setItems(FXCollections.observableArrayList(
                amiService.amis(SessionUtilisateur.exiger())));
        if (amiCombo.getItems().isEmpty()) {
            message.setText("Ajoutez d'abord un ami pour comparer vos bibliothèques.");
        }
    }

    @FXML
    private void surRafraichir() {
        message.setText("");
        rafraichirAmis();
    }

    @FXML
    private void surComparer() {
        Utilisateur ami = amiCombo.getValue();
        if (ami == null) {
            message.setText("Choisissez un ami.");
            return;
        }
        ComparaisonResultat resultat = comparaisonService.comparer(SessionUtilisateur.exiger(), ami);
        communs.setAll(resultat.getCommuns());
        seulementMoi.setAll(resultat.getSeulementChezMoi());
        seulementAutre.setAll(resultat.getSeulementChezAutre());
        titreSeulementAutre.setText("Chez " + ami.getPseudo() + " seulement");
        message.setText(communs.size() + " jeu(x) en commun.");
    }
}
