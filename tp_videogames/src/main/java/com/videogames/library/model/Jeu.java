package com.videogames.library.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jeu")
public class Jeu {

    public Jeu() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jeu")
    private Long id;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "description_jeu", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_sortie")
    private LocalDate dateSortie;

    @Column(name = "prix", precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(name = "url_image", length = 500)
    private String urlImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, columnDefinition = "VARCHAR(20)")
    private ModeJeu mode;

    @ManyToMany
    @JoinTable(name = "appartient",
            joinColumns = @JoinColumn(name = "id_jeu"),
            inverseJoinColumns = @JoinColumn(name = "id_genre"))
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "disponible",
            joinColumns = @JoinColumn(name = "id_jeu"),
            inverseJoinColumns = @JoinColumn(name = "id_plateforme"))
    private Set<Plateforme> plateformes = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(LocalDate dateSortie) {
        this.dateSortie = dateSortie;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public ModeJeu getMode() {
        return mode;
    }

    public void setMode(ModeJeu mode) {
        this.mode = mode;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Set<Plateforme> getPlateformes() {
        return plateformes;
    }

    public void setPlateformes(Set<Plateforme> plateformes) {
        this.plateformes = plateformes;
    }

    public boolean estMulti() {
        return mode != ModeJeu.SOLO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Jeu)) {
            return false;
        }
        Jeu that = (Jeu) o;
        if (id == null || that.id == null) {
            return this == that;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return titre;
    }
}
