package com.videogames.library.service;

import com.videogames.library.dao.EntreeBibliothequeDAO;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.Utilisateur;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Comparaison des bibliothèques de plusieurs utilisateurs.
 */
public class ComparaisonService {

    private final EntreeBibliothequeDAO entreeDAO;

    public ComparaisonService() {
        this(new EntreeBibliothequeDAO());
    }

    public ComparaisonService(EntreeBibliothequeDAO entreeDAO) {
        this.entreeDAO = entreeDAO;
    }

    /** Jeux possédés par les deux utilisateurs, et exclusivités de chacun. */
    public ComparaisonResultat comparer(Utilisateur moi, Utilisateur autre) {
        Set<Jeu> mesJeux = new LinkedHashSet<>(entreeDAO.jeuxPossedesDe(moi));
        Set<Jeu> sesJeux = new LinkedHashSet<>(entreeDAO.jeuxPossedesDe(autre));

        List<Jeu> communs = new ArrayList<>();
        List<Jeu> seulementChezMoi = new ArrayList<>();
        List<Jeu> seulementChezAutre = new ArrayList<>();

        for (Jeu jeu : mesJeux) {
            if (sesJeux.contains(jeu)) {
                communs.add(jeu);
            } else {
                seulementChezMoi.add(jeu);
            }
        }
        for (Jeu jeu : sesJeux) {
            if (!mesJeux.contains(jeu)) {
                seulementChezAutre.add(jeu);
            }
        }
        return new ComparaisonResultat(communs, seulementChezMoi, seulementChezAutre);
    }

    /** Jeux possédés par tous les membres du groupe. */
    public List<Jeu> communsGroupe(List<Utilisateur> membres) {
        if (membres == null || membres.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> ids = new ArrayList<>();
        for (Utilisateur membre : membres) {
            ids.add(membre.getId());
        }
        return entreeDAO.intersection(ids, new LinkedHashSet<>(ids).size());
    }
}
