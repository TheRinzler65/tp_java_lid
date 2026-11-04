package com.videogames.library.api;

import com.videogames.library.model.ModeJeu;

import java.time.LocalDate;
import java.util.List;

public class JeuIgdb {

    private final long igdbId;
    private final String titre;
    private final String description;
    private final LocalDate dateSortie;
    private final String urlImage;
    private final ModeJeu mode;
    private final List<String> genres;
    private final List<String> plateformes;

    public JeuIgdb(long igdbId, String titre, String description, LocalDate dateSortie,
                   String urlImage, ModeJeu mode, List<String> genres, List<String> plateformes) {
        this.igdbId = igdbId;
        this.titre = titre;
        this.description = description;
        this.dateSortie = dateSortie;
        this.urlImage = urlImage;
        this.mode = mode;
        this.genres = genres;
        this.plateformes = plateformes;
    }

    public long getIgdbId() {
        return igdbId;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public ModeJeu getMode() {
        return mode;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getPlateformes() {
        return plateformes;
    }

    @Override
    public String toString() {
        return titre + (dateSortie == null ? "" : " (" + dateSortie.getYear() + ")");
    }
}
