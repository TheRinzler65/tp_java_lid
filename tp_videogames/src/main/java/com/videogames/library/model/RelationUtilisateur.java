package com.videogames.library.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Représente RELATION_AMIE du MCD : une relation orientée entre deux
 * utilisateurs, dont le statut (en attente / acceptée / bloquée) est porté
 * par le champ "statut_relation".
 */
@Entity
@Table(name = "relation_amie",
        uniqueConstraints = @UniqueConstraint(name = "uq_relation", columnNames = {"id_emetteur", "id_cible"}))
public class RelationUtilisateur {

    public RelationUtilisateur() {
    }

    public RelationUtilisateur(Utilisateur emetteur, Utilisateur cible, StatutRelation statut) {
        this.emetteur = emetteur;
        this.cible = cible;
        this.statut = statut;
        this.dateDemande = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relation")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emetteur")
    private Utilisateur emetteur;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cible")
    private Utilisateur cible;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_relation", nullable = false, length = 20)
    private StatutRelation statut;

    @Column(name = "date_demande")
    private LocalDateTime dateDemande = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(Utilisateur emetteur) {
        this.emetteur = emetteur;
    }

    public Utilisateur getCible() {
        return cible;
    }

    public void setCible(Utilisateur cible) {
        this.cible = cible;
    }

    public StatutRelation getStatut() {
        return statut;
    }

    public void setStatut(StatutRelation statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    /** L'autre bout de la relation par rapport à l'utilisateur passé. */
    public Utilisateur autreQue(Utilisateur u) {
        if (emetteur != null && u != null && Objects.equals(emetteur.getId(), u.getId())) {
            return cible;
        }
        return emetteur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelationUtilisateur)) {
            return false;
        }
        RelationUtilisateur that = (RelationUtilisateur) o;
        if (id != null && that.id != null) {
            return id.equals(that.id);
        }
        return Objects.equals(emetteur, that.emetteur) && Objects.equals(cible, that.cible);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : Objects.hash(emetteur, cible);
    }

    @Override
    public String toString() {
        return "Relation{" + (emetteur != null ? emetteur.getPseudo() : "?")
                + " -> " + (cible != null ? cible.getPseudo() : "?")
                + " | " + statut + "}";
    }
}
