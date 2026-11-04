package com.videogames.library.controller;

import com.videogames.library.dao.JeuDAO;
import com.videogames.library.model.EntreeBibliotheque;
import com.videogames.library.model.Genre;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.Plateforme;
import com.videogames.library.model.Utilisateur;
import com.videogames.library.service.BibliothequeService;
import com.videogames.library.util.SessionUtilisateur;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BibliothequeController {

    private final BibliothequeService bibliothequeService = new BibliothequeService();
    private final JeuDAO jeuDAO = new JeuDAO();
    private final ObservableList<EntreeBibliotheque> entrees = FXCollections.observableArrayList();

    @FXML private TextField rechercheField;
    @FXML private CheckBox filtrePossede;
    @FXML private CheckBox filtreAAcheter;
    @FXML private CheckBox filtreFavori;
    @FXML private Label message;

    @FXML private TableView<EntreeBibliotheque> table;
    @FXML private TableColumn<EntreeBibliotheque, String> colTitre;
    @FXML private TableColumn<EntreeBibliotheque, String> colGenres;
    @FXML private TableColumn<EntreeBibliotheque, String> colPlateformes;
    @FXML private TableColumn<EntreeBibliotheque, String> colMode;
    @FXML private TableColumn<EntreeBibliotheque, String> colPossede;
    @FXML private TableColumn<EntreeBibliotheque, String> colAAcheter;
    @FXML private TableColumn<EntreeBibliotheque, String> colFavori;
    @FXML private TableColumn<EntreeBibliotheque, String> colDate;

    @FXML
    private void initialize() {
        colTitre.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getJeu().getTitre()));
        colGenres.setCellValueFactory(d -> new ReadOnlyStringWrapper(joindre(
                d.getValue().getJeu().getGenres().stream().map(Genre::getNom).collect(Collectors.toList()))));
        colPlateformes.setCellValueFactory(d -> new ReadOnlyStringWrapper(joindre(
                d.getValue().getJeu().getPlateformes().stream().map(Plateforme::getNom).collect(Collectors.toList()))));
        colMode.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getJeu().getMode() == null ? "" : d.getValue().getJeu().getMode().name()));
        colPossede.setCellValueFactory(d -> new ReadOnlyStringWrapper(ouiNon(d.getValue().isPossede())));
        colAAcheter.setCellValueFactory(d -> new ReadOnlyStringWrapper(ouiNon(d.getValue().isAAcheter())));
        colFavori.setCellValueFactory(d -> new ReadOnlyStringWrapper(ouiNon(d.getValue().isFavori())));
        colDate.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getDateAjout() == null ? "" : d.getValue().getDateAjout().toString()));

        table.setItems(entrees);
        rechercheField.textProperty().addListener((o, a, b) -> rafraichir());
        filtrePossede.selectedProperty().addListener((o, a, b) -> rafraichir());
        filtreAAcheter.selectedProperty().addListener((o, a, b) -> rafraichir());
        filtreFavori.selectedProperty().addListener((o, a, b) -> rafraichir());
        rafraichir();
    }

    private void rafraichir() {
        Utilisateur utilisateur = SessionUtilisateur.exiger();
        String recherche = rechercheField.getText() == null
                ? "" : rechercheField.getText().trim().toLowerCase(Locale.ROOT);

        List<EntreeBibliotheque> filtrees = new ArrayList<>();
        for (EntreeBibliotheque e : bibliothequeService.lister(utilisateur)) {
            if (!recherche.isEmpty()
                    && !e.getJeu().getTitre().toLowerCase(Locale.ROOT).contains(recherche)) {
                continue;
            }
            if (filtrePossede.isSelected() && !e.isPossede()) {
                continue;
            }
            if (filtreAAcheter.isSelected() && !e.isAAcheter()) {
                continue;
            }
            if (filtreFavori.isSelected() && !e.isFavori()) {
                continue;
            }
            filtrees.add(e);
        }
        entrees.setAll(filtrees);
    }

    @FXML
    private void surAjouter() {
        message.setText("");
        Utilisateur utilisateur = SessionUtilisateur.exiger();
        List<Jeu> disponibles = jeuDAO.trouverTous().stream()
                .filter(j -> bibliothequeService.entree(utilisateur, j) == null)
                .collect(Collectors.toList());
        if (disponibles.isEmpty()) {
            message.setText("Tous les jeux sont déjà dans votre bibliothèque.");
            return;
        }
        ChoiceDialog<Jeu> dialog = new ChoiceDialog<>(disponibles.get(0), disponibles);
        dialog.setTitle("Ajouter un jeu");
        dialog.setHeaderText("Choisissez un jeu à ajouter à votre bibliothèque");
        dialog.setContentText("Jeu :");
        if (dialog.showAndWait().orElse(null) == null) {
            return;
        }
        bibliothequeService.ajouter(utilisateur, dialog.getSelectedItem());
        rafraichir();
    }

    @FXML
    private void surRetirer() {
        EntreeBibliotheque selection = selection();
        if (selection == null) {
            return;
        }
        if (confirmer("Retirer \"" + selection.getJeu().getTitre() + "\" de la bibliothèque ?")) {
            bibliothequeService.retirer(SessionUtilisateur.exiger(), selection.getJeu());
            rafraichir();
        }
    }

    @FXML
    private void surPossede() {
        EntreeBibliotheque selection = selection();
        if (selection == null) {
            return;
        }
        bibliothequeService.modifier(SessionUtilisateur.exiger(), selection.getJeu(),
                true, false, selection.isFavori());
        rafraichir();
    }

    @FXML
    private void surAAcheter() {
        EntreeBibliotheque selection = selection();
        if (selection == null) {
            return;
        }
        bibliothequeService.marquerAAcheter(SessionUtilisateur.exiger(), selection.getJeu());
        rafraichir();
    }

    @FXML
    private void surFavori() {
        EntreeBibliotheque selection = selection();
        if (selection == null) {
            return;
        }
        bibliothequeService.basculerFavori(SessionUtilisateur.exiger(), selection.getJeu());
        rafraichir();
    }

    private EntreeBibliotheque selection() {
        EntreeBibliotheque selection = table.getSelectionModel().getSelectedItem();
        if (selection == null) {
            message.setText("Sélectionnez d'abord un jeu dans la table.");
        }
        return selection;
    }

    private boolean confirmer(String texte) {
        ButtonType reponse = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION, texte,
                ButtonType.OK, ButtonType.CANCEL).showAndWait().orElse(ButtonType.CANCEL);
        return reponse == ButtonType.OK;
    }

    private static String joindre(List<String> valeurs) {
        return valeurs.stream().sorted().collect(Collectors.joining(", "));
    }

    private static String ouiNon(boolean valeur) {
        return valeur ? "Oui" : "Non";
    }
}
