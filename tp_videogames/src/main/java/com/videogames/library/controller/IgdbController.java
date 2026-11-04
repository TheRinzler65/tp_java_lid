package com.videogames.library.controller;

import com.videogames.library.api.IgdbClient;
import com.videogames.library.api.JeuIgdb;
import com.videogames.library.model.Jeu;
import com.videogames.library.service.BibliothequeService;
import com.videogames.library.service.ImportJeuService;
import com.videogames.library.util.SessionUtilisateur;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.stream.Collectors;

public class IgdbController {

    private final IgdbClient igdbClient = new IgdbClient();
    private final ImportJeuService importService = new ImportJeuService();
    private final BibliothequeService bibliothequeService = new BibliothequeService();

    private final ObservableList<JeuIgdb> resultats = FXCollections.observableArrayList();

    @FXML private TextField queryField;
    @FXML private Button rechercheButton;
    @FXML private Label message;
    @FXML private TableView<JeuIgdb> table;
    @FXML private TableColumn<JeuIgdb, String> colTitre;
    @FXML private TableColumn<JeuIgdb, String> colDate;
    @FXML private TableColumn<JeuIgdb, String> colMode;
    @FXML private TableColumn<JeuIgdb, String> colGenres;
    @FXML private TableColumn<JeuIgdb, String> colPlateformes;

    @FXML
    private void initialize() {
        colTitre.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getTitre()));
        colDate.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getDateSortie() == null ? "" : d.getValue().getDateSortie().toString()));
        colMode.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getMode() == null ? "" : d.getValue().getMode().name()));
        colGenres.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                String.join(", ", d.getValue().getGenres())));
        colPlateformes.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                String.join(", ", d.getValue().getPlateformes())));
        table.setItems(resultats);

        if (!igdbClient.estConfigure()) {
            rechercheButton.setDisable(true);
            message.setText("IGDB non configuré : définissez les variables d'environnement "
                    + "IGDB_CLIENT_ID et IGDB_CLIENT_SECRET puis relancez l'application.");
        }
    }

    @FXML
    private void surRechercher() {
        message.setText("");
        String texte = queryField.getText();
        if (texte == null || texte.trim().isEmpty()) {
            message.setText("Saisissez un titre à rechercher.");
            return;
        }

        Task<List<JeuIgdb>> tache = new Task<>() {
            @Override
            protected List<JeuIgdb> call() throws Exception {
                return igdbClient.rechercher(texte.trim(), 20);
            }
        };

        tache.setOnSucceeded(e -> {
            resultats.setAll(tache.getValue());
            rechercheButton.setDisable(false);
            if (resultats.isEmpty()) {
                message.setText("Aucun jeu trouvé sur IGDB.");
            }
        });
        tache.setOnFailed(e -> {
            rechercheButton.setDisable(false);
            Throwable erreur = tache.getException();
            message.setText("Erreur IGDB : " + (erreur == null ? "inconnue" : erreur.getMessage()));
        });

        rechercheButton.setDisable(true);
        Thread thread = new Thread(tache, "igdb-recherche");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void surImporter() {
        message.setText("");
        JeuIgdb selection = table.getSelectionModel().getSelectedItem();
        if (selection == null) {
            message.setText("Sélectionnez un jeu IGDB à importer.");
            return;
        }
        try {
            Jeu jeu = importService.importer(selection);
            bibliothequeService.ajouter(SessionUtilisateur.exiger(), jeu);
            message.setText("\"" + jeu.getTitre() + "\" importé et ajouté à votre bibliothèque.");
        } catch (RuntimeException e) {
            message.setText("Erreur d'import : " + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static String joindre(List<String> valeurs) {
        return valeurs.stream().sorted().collect(Collectors.joining(", "));
    }
}
