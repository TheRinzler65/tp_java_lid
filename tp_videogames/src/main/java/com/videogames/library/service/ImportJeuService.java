package com.videogames.library.service;

import com.videogames.library.api.JeuIgdb;
import com.videogames.library.dao.GenreDAO;
import com.videogames.library.dao.JeuDAO;
import com.videogames.library.dao.PlateformeDAO;
import com.videogames.library.model.Genre;
import com.videogames.library.model.Jeu;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ImportJeuService {

    private final JeuDAO jeuDAO;
    private final GenreDAO genreDAO;
    private final PlateformeDAO plateformeDAO;

    public ImportJeuService() {
        this(new JeuDAO(), new GenreDAO(), new PlateformeDAO());
    }

    public ImportJeuService(JeuDAO jeuDAO, GenreDAO genreDAO, PlateformeDAO plateformeDAO) {
        this.jeuDAO = jeuDAO;
        this.genreDAO = genreDAO;
        this.plateformeDAO = plateformeDAO;
    }

    public Jeu importer(JeuIgdb source) {
        if (source == null || source.getTitre() == null || source.getTitre().isBlank()) {
            throw new IllegalArgumentException("Jeu IGDB invalide : titre manquant.");
        }
        List<Jeu> existants = jeuDAO.rechercherParNom(source.getTitre());
        for (Jeu existant : existants) {
            if (existant.getTitre().equalsIgnoreCase(source.getTitre())) {
                return existant;
            }
        }

        Jeu jeu = new Jeu();
        jeu.setTitre(source.getTitre());
        jeu.setDescription(source.getDescription());
        jeu.setDateSortie(source.getDateSortie());
        jeu.setUrlImage(source.getUrlImage());
        jeu.setMode(source.getMode() == null
                ? com.videogames.library.model.ModeJeu.SOLO_ET_ONLINE
                : source.getMode());

        Set<Genre> genres = new LinkedHashSet<>();
        for (String nom : source.getGenres()) {
            genres.add(genreDAO.trouverOuCreer(nom));
        }
        jeu.setGenres(genres);

        Set<com.videogames.library.model.Plateforme> plateformes = new LinkedHashSet<>();
        for (String nom : source.getPlateformes()) {
            plateformes.add(plateformeDAO.trouverOuCreer(nom));
        }
        jeu.setPlateformes(plateformes);

        jeuDAO.sauver(jeu);
        return jeu;
    }
}
