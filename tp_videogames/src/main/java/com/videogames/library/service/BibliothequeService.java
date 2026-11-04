package com.videogames.library.service;

import com.videogames.library.dao.EntreeBibliothequeDAO;
import com.videogames.library.model.EntreeBibliotheque;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.Utilisateur;

import java.util.List;

/**
 * Gestion de la bibliothèque d'un utilisateur :
 * ajout / modification / suppression d'un jeu, et marqueurs
 * possédé / à acheter / favori.
 */
public class BibliothequeService {

    private final EntreeBibliothequeDAO entreeDAO;

    public BibliothequeService() {
        this(new EntreeBibliothequeDAO());
    }

    public BibliothequeService(EntreeBibliothequeDAO entreeDAO) {
        this.entreeDAO = entreeDAO;
    }

    public List<EntreeBibliotheque> lister(Utilisateur utilisateur) {
        return entreeDAO.listerJeuxDe(utilisateur);
    }

    public EntreeBibliotheque entree(Utilisateur utilisateur, Jeu jeu) {
        return entreeDAO.trouver(utilisateur, jeu);
    }

    /** Ajoute le jeu à la bibliothèque (possédé), ou met à jour l'entrée existante. */
    public EntreeBibliotheque ajouter(Utilisateur utilisateur, Jeu jeu) {
        EntreeBibliotheque entree = entreeDAO.trouver(utilisateur, jeu);
        if (entree == null) {
            entree = new EntreeBibliotheque(utilisateur, jeu);
        }
        entree.setPossede(true);
        entree.setAAcheter(false);
        entreeDAO.sauver(entree);
        return entree;
    }

    public void retirer(Utilisateur utilisateur, Jeu jeu) {
        entreeDAO.supprimer(utilisateur, jeu);
    }

    /**
     * Met à jour les marqueurs d'une entrée. Un jeu marqué "possédé" ne peut pas
     * rester dans la liste "à acheter".
     */
    public EntreeBibliotheque modifier(Utilisateur utilisateur, Jeu jeu,
                                       boolean possede, boolean aAcheter, boolean favori) {
        EntreeBibliotheque entree = entreeDAO.trouver(utilisateur, jeu);
        if (entree == null) {
            entree = new EntreeBibliotheque(utilisateur, jeu);
        }
        entree.setPossede(possede);
        entree.setAAcheter(possede ? false : aAcheter);
        entree.setFavori(favori);
        entreeDAO.sauver(entree);
        return entree;
    }

    /** Marque le jeu comme favori (créé l'entrée si nécessaire). */
    public EntreeBibliotheque basculerFavori(Utilisateur utilisateur, Jeu jeu) {
        EntreeBibliotheque entree = entreeDAO.trouver(utilisateur, jeu);
        if (entree == null) {
            entree = new EntreeBibliotheque(utilisateur, jeu);
        }
        entree.setFavori(!entree.isFavori());
        entreeDAO.sauver(entree);
        return entree;
    }

    /** Marque le jeu comme "à acheter" (créé l'entrée si nécessaire). */
    public EntreeBibliotheque marquerAAcheter(Utilisateur utilisateur, Jeu jeu) {
        EntreeBibliotheque entree = entreeDAO.trouver(utilisateur, jeu);
        if (entree == null) {
            entree = new EntreeBibliotheque(utilisateur, jeu);
        }
        entree.setAAcheter(true);
        entree.setPossede(false);
        entreeDAO.sauver(entree);
        return entree;
    }
}
