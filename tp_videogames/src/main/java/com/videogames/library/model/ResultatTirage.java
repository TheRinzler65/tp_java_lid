package com.videogames.library.model;

public class ResultatTirage {

    private Jeu jeu;
    private boolean horsBibliothequeCommune;

    public ResultatTirage() {
    }

    public ResultatTirage(Jeu jeu, boolean horsBibliothequeCommune) {
        this.jeu = jeu;
        this.horsBibliothequeCommune = horsBibliothequeCommune;
    }

    public Jeu getJeu() {
        return jeu;
    }

    public void setJeu(Jeu jeu) {
        this.jeu = jeu;
    }

    public boolean isHorsBibliothequeCommune() {
        return horsBibliothequeCommune;
    }

    public void setHorsBibliothequeCommune(boolean horsBibliothequeCommune) {
        this.horsBibliothequeCommune = horsBibliothequeCommune;
    }
}
