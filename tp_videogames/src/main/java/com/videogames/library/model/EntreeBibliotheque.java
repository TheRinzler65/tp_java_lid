package com.videogames.library.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "bibliotheque")
public class EntreeBibliotheque {

    public EntreeBibliotheque() {
    }

    public EntreeBibliotheque(Utilisateur utilisateur, Jeu jeu) {
        this.utilisateur = utilisateur;
        this.jeu = jeu;
        this.dateAjout = LocalDate.now();
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_jeu")
    private Jeu jeu;

    /** Le jeu est physiquement (ou numériquement) possédé. */
    @Column(name = "possede", nullable = false)
    private boolean possede;

    /** Le jeu est dans la liste "à acheter". */
    @Column(name = "a_acheter", nullable = false)
    private boolean aAcheter;

    /** Le jeu est marqué favori. */
    @Column(name = "favori", nullable = false)
    private boolean favori;

    @Column(name = "date_ajout")
    private LocalDate dateAjout;

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Jeu getJeu() {
        return jeu;
    }

    public void setJeu(Jeu jeu) {
        this.jeu = jeu;
    }

    public boolean isPossede() {
        return possede;
    }

    public void setPossede(boolean possede) {
        this.possede = possede;
    }

    public boolean isAAcheter() {
        return aAcheter;
    }

    public void setAAcheter(boolean aAcheter) {
        this.aAcheter = aAcheter;
    }

    public boolean isFavori() {
        return favori;
    }

    public void setFavori(boolean favori) {
        this.favori = favori;
    }

    public LocalDate getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntreeBibliotheque)) {
            return false;
        }
        EntreeBibliotheque that = (EntreeBibliotheque) o;
        Long uid = utilisateur == null ? null : utilisateur.getId();
        Long uid2 = that.utilisateur == null ? null : that.utilisateur.getId();
        Long jid = jeu == null ? null : jeu.getId();
        Long jid2 = that.jeu == null ? null : that.jeu.getId();
        return Objects.equals(uid, uid2) && Objects.equals(jid, jid2);
    }

    @Override
    public int hashCode() {
        Long uid = utilisateur == null ? null : utilisateur.getId();
        Long jid = jeu == null ? null : jeu.getId();
        return Objects.hash(uid, jid);
    }

    @Override
    public String toString() {
        String pseudo = utilisateur != null ? utilisateur.getPseudo() : "?";
        String titre = jeu != null ? jeu.getTitre() : "?";
        return "EntreeBibliotheque{" + pseudo + " | " + titre
                + " | possede=" + possede
                + ", aAcheter=" + aAcheter
                + ", favori=" + favori + "}";
    }
}
