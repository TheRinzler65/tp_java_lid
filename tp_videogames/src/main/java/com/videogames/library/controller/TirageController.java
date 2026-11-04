package com.videogames.library.controller;

import com.videogames.library.dao.GenreDAO;
import com.videogames.library.dao.JeuDAO;
import com.videogames.library.dao.PlateformeDAO;
import com.videogames.library.model.Genre;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.Plateforme;
import com.videogames.library.model.ResultatTirage;
import com.videogames.library.model.Utilisateur;
import com.videogames.library.service.AmiService;
import com.videogames.library.service.FiltreTirage;
import com.videogames.library.service.TirageService;
import com.videogames.library.util.SessionUtilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TirageController {

    private final AmiService amiService = new AmiService();
    private final JeuDAO jeuDAO = new JeuDAO();
    private final GenreDAO genreDAO = new GenreDAO();
    private final PlateformeDAO plateformeDAO = new PlateformeDAO();
    private final TirageService tirageService = new TirageService();

    private final ObservableList<Utilisateur> amis = FXCollections.observableArrayList();
    private final ObservableList<Jeu> exclusions = FXCollections.observableArrayList();
    private final ObservableList<Jeu> ajouts = FXCollections.observableArrayList();

    @FXML private RadioButton multiRadio;
    @FXML private RadioButton soloRadio;
    @FXML private ListView<Utilisateur> amisList;
    @FXML private ComboBox<Genre> genreCombo;
    @FXML private ComboBox<Plateforme> plateformeCombo;
    @FXML private TextField prixMaxField;
    @FXML private ListView<Jeu> exclusionsList;
    @FXML private ListView<Jeu> ajoutsList;
    @FXML private Label message;
    @FXML private Label resultatTitre;
    @FXML private Label resultatDetails;

    @FXML
    private void initialize() {
        amisList.setItems(amis);
        exclusionsList.setItems(exclusions);
        ajoutsList.setItems(ajouts);
        amisList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        genreCombo.setItems(FXCollections.observableArrayList(genreDAO.trouverTous()));
        plateformeCombo.setItems(FXCollections.observableArrayList(plateformeDAO.trouverTous()));
        amis.setAll(amiService.amis(SessionUtilisateur.exiger()));

        multiRadio.selectedProperty().addListener((o, a, multi) -> amisList.setDisable(!multi));
    }

    @FXML
    private void surTirer() {
        message.setText("");
        try {
            FiltreTirage filtre = construireFiltre();
            if (filtre == null) {
                return;
            }

            ResultatTirage resultat;
            if (multiRadio.isSelected()) {
                List<Utilisateur> membres = new ArrayList<>();
                membres.add(SessionUtilisateur.exiger());
                membres.addAll(amisList.getSelectionModel().getSelectedItems());
                if (membres.size() < 2) {
                    message.setText("Sélectionnez au moins un ami pour un tirage multijoueur.");
                    return;
                }
                resultat = tirageService.tirerPourGroupe(membres, filtre, exclusions, ajouts);
            } else {
                resultat = tirageService.tirerSolo(
                        SessionUtilisateur.exiger(), filtre, exclusions, ajouts);
            }

            afficher(resultat);
        } catch (RuntimeException e) {
            message.setText("Erreur : " + e.getMessage());
        }
    }

    private FiltreTirage construireFiltre() {
        FiltreTirage filtre = new FiltreTirage();
        if (genreCombo.getValue() != null) {
            filtre.ajouterGenre(genreCombo.getValue().getId());
        }
        if (plateformeCombo.getValue() != null) {
            filtre.ajouterPlateforme(plateformeCombo.getValue().getId());
        }
        String textePrix = prixMaxField.getText();
        if (textePrix != null && !textePrix.trim().isEmpty()) {
            try {
                filtre.setPrixMax(new BigDecimal(textePrix.trim().replace(',', '.')));
            } catch (NumberFormatException e) {
                message.setText("Prix maximum invalide.");
                return null;
            }
        }
        return filtre;
    }

    private void afficher(ResultatTirage resultat) {
        if (resultat == null) {
            resultatTitre.setText("Aucun jeu");
            resultatDetails.setText("Aucun jeu ne correspond aux critères (ou bibliothèque commune vide).");
            return;
        }
        Jeu jeu = resultat.getJeu();
        resultatTitre.setText(jeu.getTitre());

        StringBuilder details = new StringBuilder();
        details.append("Mode : ").append(jeu.getMode()).append("   |   ");
        details.append("Genres : ").append(jeu.getGenres().stream()
                .map(Genre::getNom).sorted().collect(Collectors.joining(", ")));
        details.append("   |   Plateformes : ").append(jeu.getPlateformes().stream()
                .map(Plateforme::getNom).sorted().collect(Collectors.joining(", ")));

        if (resultat.isHorsBibliothequeCommune()) {
            details.append("\nHors bibliothèque commune — prix : ")
                    .append(jeu.getPrix() == null ? "inconnu" : jeu.getPrix() + " €");
        }
        resultatDetails.setText(details.toString());
    }

    @FXML
    private void surAjouterExclusion() {
        Jeu jeu = choisirJeu("Exclure un jeu", "Jeu à exclure du tirage :");
        if (jeu != null && !exclusions.contains(jeu)) {
            exclusions.add(jeu);
        }
    }

    @FXML
    private void surRetirerExclusion() {
        Jeu jeu = exclusionsList.getSelectionModel().getSelectedItem();
        if (jeu != null) {
            exclusions.remove(jeu);
        }
    }

    @FXML
    private void surAjouterAjout() {
        Jeu jeu = choisirJeu("Ajouter un jeu",
                "Jeu hors bibliothèque commune à proposer au tirage :");
        if (jeu != null && !ajouts.contains(jeu)) {
            ajouts.add(jeu);
        }
    }

    @FXML
    private void surRetirerAjout() {
        Jeu jeu = ajoutsList.getSelectionModel().getSelectedItem();
        if (jeu != null) {
            ajouts.remove(jeu);
        }
    }

    private Jeu choisirJeu(String titre, String entete) {
        List<Jeu> jeux = jeuDAO.trouverTous();
        if (jeux.isEmpty()) {
            message.setText("Aucun jeu disponible.");
            return null;
        }
        ChoiceDialog<Jeu> dialog = new ChoiceDialog<>(jeux.get(0), jeux);
        dialog.setTitle(titre);
        dialog.setHeaderText(entete);
        dialog.setContentText("Jeu :");
        return dialog.showAndWait().orElse(null);
    }
}
