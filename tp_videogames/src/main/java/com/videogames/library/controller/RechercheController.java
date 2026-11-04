package com.videogames.library.controller;

import com.videogames.library.dao.GenreDAO;
import com.videogames.library.dao.JeuDAO;
import com.videogames.library.dao.PlateformeDAO;
import com.videogames.library.model.Genre;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import com.videogames.library.model.Plateforme;
import com.videogames.library.service.BibliothequeService;
import com.videogames.library.util.SessionUtilisateur;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RechercheController {

    private final JeuDAO jeuDAO = new JeuDAO();
    private final GenreDAO genreDAO = new GenreDAO();
    private final PlateformeDAO plateformeDAO = new PlateformeDAO();
    private final BibliothequeService bibliothequeService = new BibliothequeService();

    private final ObservableList<Jeu> resultats = FXCollections.observableArrayList();

    @FXML private TextField titreField;
    @FXML private ComboBox<Genre> genreCombo;
    @FXML private ComboBox<Plateforme> plateformeCombo;
    @FXML private ComboBox<ModeJeu> modeCombo;
    @FXML private TextField prixMaxField;
    @FXML private CheckBox multiSeulement;
    @FXML private Label message;

    @FXML private TableView<Jeu> table;
    @FXML private TableColumn<Jeu, String> colTitre;
    @FXML private TableColumn<Jeu, String> colGenres;
    @FXML private TableColumn<Jeu, String> colPlateformes;
    @FXML private TableColumn<Jeu, String> colMode;
    @FXML private TableColumn<Jeu, String> colDate;
    @FXML private TableColumn<Jeu, String> colPrix;

    @FXML
    private void initialize() {
        genreCombo.setItems(FXCollections.observableArrayList(genreDAO.trouverTous()));
        plateformeCombo.setItems(FXCollections.observableArrayList(plateformeDAO.trouverTous()));
        modeCombo.setItems(FXCollections.observableArrayList(ModeJeu.values()));

        colTitre.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getTitre()));
        colGenres.setCellValueFactory(d -> new ReadOnlyStringWrapper(joindreNoms(
                d.getValue().getGenres().stream().map(Genre::getNom).collect(Collectors.toList()))));
        colPlateformes.setCellValueFactory(d -> new ReadOnlyStringWrapper(joindreNoms(
                d.getValue().getPlateformes().stream().map(Plateforme::getNom).collect(Collectors.toList()))));
        colMode.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getMode() == null ? "" : d.getValue().getMode().name()));
        colDate.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getDateSortie() == null ? "" : d.getValue().getDateSortie().toString()));
        colPrix.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().getPrix() == null ? "" : d.getValue().getPrix() + " €"));

        table.setItems(resultats);
        surRechercher();
    }

    @FXML
    private void surRechercher() {
        message.setText("");
        BigDecimal prixMax = null;
        String textePrix = prixMaxField.getText();
        if (textePrix != null && !textePrix.trim().isEmpty()) {
            try {
                prixMax = new BigDecimal(textePrix.trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                message.setText("Prix maximum invalide.");
                return;
            }
        }

        List<Long> genres = genreCombo.getValue() == null
                ? new ArrayList<>() : List.of(genreCombo.getValue().getId());
        List<Long> plateformes = plateformeCombo.getValue() == null
                ? new ArrayList<>() : List.of(plateformeCombo.getValue().getId());

        List<Jeu> jeux = jeuDAO.rechercher(titreField.getText(), genres, plateformes,
                modeCombo.getValue(), multiSeulement.isSelected(), prixMax);
        resultats.setAll(jeux);
        if (jeux.isEmpty()) {
            message.setText("Aucun jeu ne correspond à ces critères.");
        }
    }

    @FXML
    private void surAjouter() {
        Jeu jeu = selection();
        if (jeu == null) {
            return;
        }
        bibliothequeService.ajouter(SessionUtilisateur.exiger(), jeu);
        message.setText("\"" + jeu.getTitre() + "\" ajouté à votre bibliothèque.");
    }

    @FXML
    private void surAAcheter() {
        Jeu jeu = selection();
        if (jeu == null) {
            return;
        }
        bibliothequeService.marquerAAcheter(SessionUtilisateur.exiger(), jeu);
        message.setText("\"" + jeu.getTitre() + "\" marqué comme à acheter.");
    }

    @FXML
    private void surFavori() {
        Jeu jeu = selection();
        if (jeu == null) {
            return;
        }
        boolean favori = bibliothequeService.basculerFavori(SessionUtilisateur.exiger(), jeu).isFavori();
        message.setText("\"" + jeu.getTitre() + "\" " + (favori ? "ajouté aux" : "retiré des") + " favoris.");
    }

    private Jeu selection() {
        Jeu jeu = table.getSelectionModel().getSelectedItem();
        if (jeu == null) {
            message.setText("Sélectionnez d'abord un jeu.");
        }
        return jeu;
    }

    private static String joindreNoms(List<String> valeurs) {
        return valeurs.stream().sorted().collect(Collectors.joining(", "));
    }
}
