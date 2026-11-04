package com.videogames.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String mdp;

    @Column(nullable = false, unique = true, length = 100)
    private String pseudo;

    @Column(columnDefinition = "TEXT")
    private String description;

    public Utilisateur() {
    }

    public Utilisateur(String _email, String _mdp, String _pseudo) {
        this.email = _email;
        this.mdp = _mdp;
        this.pseudo = _pseudo;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utilisateur)) {
            return false;
        }
        Utilisateur that = (Utilisateur) o;
        // identité forte par id ; à défaut (entité non encore persistée) : identité objet
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
        return pseudo;
    }
}
