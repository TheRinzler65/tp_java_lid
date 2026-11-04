package com.videogames.library.controller;

import com.videogames.library.model.RelationUtilisateur;
import com.videogames.library.model.Utilisateur;
import com.videogames.library.service.AmiService;
import com.videogames.library.util.SessionUtilisateur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.stream.Collectors;

public class AmisController {

    private final AmiService amiService = new AmiService();

    private final ObservableList<Utilisateur> resultats = FXCollections.observableArrayList();
    private final ObservableList<Utilisateur> amis = FXCollections.observableArrayList();
    private final ObservableList<RelationUtilisateur> demandes = FXCollections.observableArrayList();
    private final ObservableList<Utilisateur> bloques = FXCollections.observableArrayList();

    @FXML private TextField rechercheField;
    @FXML private ListView<Utilisateur> resultatsList;
    @FXML private ListView<Utilisateur> amisList;
    @FXML private ListView<RelationUtilisateur> demandesList;
    @FXML private ListView<Utilisateur> bloquesList;
    @FXML private Label message;

    @FXML
    private void initialize() {
        resultatsList.setItems(resultats);
        amisList.setItems(amis);
        demandesList.setItems(demandes);
        bloquesList.setItems(bloques);

        resultatsList.setCellFactory(v -> celluleUtilisateur());
        amisList.setCellFactory(v -> celluleUtilisateur());
        bloquesList.setCellFactory(v -> celluleUtilisateur());

        demandesList.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(RelationUtilisateur relation, boolean vide) {
                super.updateItem(relation, vide);
                setText(vide || relation == null || relation.getEmetteur() == null
                        ? null
                        : relation.getEmetteur().getPseudo());
            }
        });

        rafraichir();
    }

    private ListCell<Utilisateur> celluleUtilisateur() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Utilisateur utilisateur, boolean vide) {
                super.updateItem(utilisateur, vide);
                setText(vide || utilisateur == null ? null : utilisateur.getPseudo());
            }
        };
    }

    private void rafraichir() {
        Utilisateur moi = SessionUtilisateur.exiger();
        amis.setAll(amiService.amis(moi));
        demandes.setAll(amiService.demandesRecues(moi));
        bloques.setAll(amiService.bloques(moi));
    }

    @FXML
    private void surRechercher() {
        message.setText("");
        Utilisateur moi = SessionUtilisateur.exiger();
        List<Utilisateur> trouves = amiService.rechercherUtilisateurs(rechercheField.getText())
                .stream()
                .filter(u -> !u.getId().equals(moi.getId()))
                .collect(Collectors.toList());
        resultats.setAll(trouves);
        if (trouves.isEmpty()) {
            message.setText("Aucun utilisateur trouvé.");
        }
    }

    @FXML
    private void surDemander() {
        Utilisateur cible = resultatsList.getSelectionModel().getSelectedItem();
        if (cible == null) {
            message.setText("Sélectionnez un utilisateur à ajouter.");
            return;
        }
        try {
            amiService.envoyerDemande(SessionUtilisateur.exiger(), cible);
            message.setText("Demande envoyée à " + cible.getPseudo() + ".");
            rafraichir();
        } catch (IllegalArgumentException e) {
            message.setText(e.getMessage());
        }
    }

    @FXML
    private void surAccepter() {
        RelationUtilisateur relation = demandesList.getSelectionModel().getSelectedItem();
        if (relation == null) {
            message.setText("Sélectionnez une demande à accepter.");
            return;
        }
        amiService.accepter(relation);
        message.setText("Demande acceptée.");
        rafraichir();
    }

    @FXML
    private void surRefuser() {
        RelationUtilisateur relation = demandesList.getSelectionModel().getSelectedItem();
        if (relation == null) {
            message.setText("Sélectionnez une demande à refuser.");
            return;
        }
        amiService.refuser(relation);
        message.setText("Demande refusée.");
        rafraichir();
    }

    @FXML
    private void surRetirer() {
        Utilisateur ami = amisList.getSelectionModel().getSelectedItem();
        if (ami == null) {
            message.setText("Sélectionnez un ami à retirer.");
            return;
        }
        amiService.supprimer(SessionUtilisateur.exiger(), ami);
        message.setText(ami.getPseudo() + " a été retiré de vos amis.");
        rafraichir();
    }

    @FXML
    private void surBloquer() {
        Utilisateur ami = amisList.getSelectionModel().getSelectedItem();
        if (ami == null) {
            message.setText("Sélectionnez un ami à bloquer.");
            return;
        }
        amiService.bloquer(SessionUtilisateur.exiger(), ami);
        message.setText(ami.getPseudo() + " a été bloqué.");
        rafraichir();
    }

    @FXML
    private void surDebloquer() {
        Utilisateur bloque = bloquesList.getSelectionModel().getSelectedItem();
        if (bloque == null) {
            message.setText("Sélectionnez un utilisateur à débloquer.");
            return;
        }
        amiService.debloquer(SessionUtilisateur.exiger(), bloque);
        message.setText(bloque.getPseudo() + " a été débloqué.");
        rafraichir();
    }
}
