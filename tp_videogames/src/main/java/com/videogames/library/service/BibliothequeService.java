package com.videogames.library.service;

import com.videogames.library.dao.EntreeBibliothequeDAO;
import com.videogames.library.model.EntreeBibliotheque;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.Utilisateur;

import java.util.List;

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

    public EntreeBibliotheque basculerFavori(Utilisateur utilisateur, Jeu jeu) {
        EntreeBibliotheque entree = entreeDAO.trouver(utilisateur, jeu);
        if (entree == null) {
            entree = new EntreeBibliotheque(utilisateur, jeu);
        }
        entree.setFavori(!entree.isFavori());
        entreeDAO.sauver(entree);
        return entree;
    }

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
