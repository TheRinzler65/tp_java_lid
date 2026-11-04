package com.videogames.library.service;

import com.videogames.library.model.Jeu;

import java.util.List;

public class ComparaisonResultat {

    private final List<Jeu> communs;
    private final List<Jeu> seulementChezMoi;
    private final List<Jeu> seulementChezAutre;

    public ComparaisonResultat(List<Jeu> communs, List<Jeu> seulementChezMoi, List<Jeu> seulementChezAutre) {
        this.communs = communs;
        this.seulementChezMoi = seulementChezMoi;
        this.seulementChezAutre = seulementChezAutre;
    }

    public List<Jeu> getCommuns() {
        return communs;
    }

    public List<Jeu> getSeulementChezMoi() {
        return seulementChezMoi;
    }

    public List<Jeu> getSeulementChezAutre() {
        return seulementChezAutre;
    }
}
