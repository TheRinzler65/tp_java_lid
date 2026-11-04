package com.videogames.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plateforme")
public class Plateforme {

    public Plateforme() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plateforme")
    private Long id;

    @Column(name = "nom_plateforme", nullable = false, unique = true, length = 100)
    private String nom;

    public Plateforme(String nom) {
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
        if (!(o instanceof Plateforme)) {
            return false;
        }
        Plateforme that = (Plateforme) o;
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