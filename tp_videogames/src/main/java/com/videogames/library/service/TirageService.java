package com.videogames.library.service;

import com.videogames.library.dao.EntreeBibliothequeDAO;
import com.videogames.library.model.Genre;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import com.videogames.library.model.Plateforme;
import com.videogames.library.model.ResultatTirage;
import com.videogames.library.model.Utilisateur;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Tirage aléatoire d'un jeu commun à un groupe (multi) ou dans la
 * bibliothèque d'un utilisateur (solo).
 *
 * Règles :
 * - multi : seuls les jeux jouables à plusieurs sont retenus (sauf si le
 *   mode est explicitement filtré autrement) ;
 * - solo : seuls les jeux jouables en solo sont retenus ;
 * - les jeux exclus sont écartés ;
 * - des jeux hors bibliothèque commune peuvent être ajoutés : ils sont
 *   marqués comme tels afin d'afficher leur prix.
 */
public class TirageService {

    private final EntreeBibliothequeDAO entreeDAO;
    private final Random random;

    public TirageService() {
        this(new EntreeBibliothequeDAO(), new Random());
    }

    public TirageService(EntreeBibliothequeDAO entreeDAO, Random random) {
        this.entreeDAO = entreeDAO;
        this.random = random;
    }

    /** Tirage multijoueur parmi les jeux possédés par tous les membres. */
    public ResultatTirage tirerPourGroupe(List<Utilisateur> membres, FiltreTirage filtre,
                                          Collection<Jeu> exclusions, Collection<Jeu> ajouts) {
        if (membres == null || membres.isEmpty()) {
            return null;
        }
        List<Long> ids = new ArrayList<>();
        for (Utilisateur membre : membres) {
            ids.add(membre.getId());
        }
        List<Jeu> communs = entreeDAO.intersection(ids, new LinkedHashSet<>(ids).size());
        return tirer(communs, true, filtre, exclusions, ajouts);
    }

    /** Tirage solo parmi les jeux possédés par l'utilisateur. */
    public ResultatTirage tirerSolo(Utilisateur utilisateur, FiltreTirage filtre,
                                    Collection<Jeu> exclusions, Collection<Jeu> ajouts) {
        if (utilisateur == null) {
            return null;
        }
        List<Jeu> communs = entreeDAO.jeuxPossedesDe(utilisateur);
        return tirer(communs, false, filtre, exclusions, ajouts);
    }

    private ResultatTirage tirer(List<Jeu> communs, boolean multi, FiltreTirage filtre,
                                 Collection<Jeu> exclusions, Collection<Jeu> ajouts) {
        Set<Jeu> exclus = exclusions == null ? Set.of() : new HashSet<>(exclusions);
        List<Jeu> candidats = new ArrayList<>();

        for (Jeu jeu : communs) {
            if (!exclus.contains(jeu) && correspond(jeu, multi, filtre)) {
                candidats.add(jeu);
            }
        }

        Set<Jeu> dejaPresents = new HashSet<>(communs);
        List<Jeu> horsBibliotheque = new ArrayList<>();
        if (ajouts != null) {
            for (Jeu jeu : ajouts) {
                if (jeu != null && !exclus.contains(jeu) && !dejaPresents.contains(jeu)
                        && correspond(jeu, multi, filtre)) {
                    horsBibliotheque.add(jeu);
                }
            }
        }

        if (candidats.isEmpty() && horsBibliotheque.isEmpty()) {
            return null;
        }

        // Tirage uniforme sur l'ensemble : jeux communs + jeux ajoutés.
        List<Jeu> tous = new ArrayList<>(candidats);
        tous.addAll(horsBibliotheque);
        Jeu choisi = tous.get(random.nextInt(tous.size()));
        return new ResultatTirage(choisi, horsBibliotheque.contains(choisi));
    }

    private boolean correspond(Jeu jeu, boolean multi, FiltreTirage filtre) {
        ModeJeu mode = jeu.getMode();
        if (mode == null) {
            return false;
        }
        if (multi && mode == ModeJeu.SOLO) {
            return false;
        }
        if (!multi && mode == ModeJeu.ONLINE) {
            return false;
        }

        FiltreTirage f = filtre == null ? new FiltreTirage() : filtre;

        if (!f.getGenreIds().isEmpty()) {
            boolean trouve = false;
            for (Genre genre : jeu.getGenres()) {
                if (f.getGenreIds().contains(genre.getId())) {
                    trouve = true;
                    break;
                }
            }
            if (!trouve) {
                return false;
            }
        }

        if (!f.getPlateformeIds().isEmpty()) {
            boolean trouve = false;
            for (Plateforme plateforme : jeu.getPlateformes()) {
                if (f.getPlateformeIds().contains(plateforme.getId())) {
                    trouve = true;
                    break;
                }
            }
            if (!trouve) {
                return false;
            }
        }

        if (f.getPrixMax() != null && jeu.getPrix() != null) {
            return jeu.getPrix().compareTo(f.getPrixMax()) <= 0;
        }
        return true;
    }
}
