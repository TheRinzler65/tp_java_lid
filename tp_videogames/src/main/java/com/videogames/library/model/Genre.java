package com.videogames.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "genre")
public class Genre {

    public Genre() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genre")
    private Long id;

    @Column(name = "nom_genre", nullable = false, unique = true, length = 100)
    private String nom;

    public Genre(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Genre)) {
            return false;
        }
        Genre that = (Genre) o;
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
        return nom;
    }
}