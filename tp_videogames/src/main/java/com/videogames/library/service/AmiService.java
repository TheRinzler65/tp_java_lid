package com.videogames.library.service;

import com.videogames.library.dao.RelationDAO;
import com.videogames.library.dao.UtilisateurDAO;
import com.videogames.library.model.RelationUtilisateur;
import com.videogames.library.model.StatutRelation;
import com.videogames.library.model.Utilisateur;

import java.util.List;
import java.util.Objects;

/**
 * Demandes d'amis, acceptation/refus, suppression et blocage.
 */
public class AmiService {

    private final RelationDAO relationDAO;
    private final UtilisateurDAO utilisateurDAO;

    public AmiService() {
        this(new RelationDAO(), new UtilisateurDAO());
    }

    public AmiService(RelationDAO relationDAO, UtilisateurDAO utilisateurDAO) {
        this.relationDAO = relationDAO;
        this.utilisateurDAO = utilisateurDAO;
    }

    public List<Utilisateur> amis(Utilisateur utilisateur) {
        return relationDAO.listerAmisDe(utilisateur);
    }

    public List<RelationUtilisateur> demandesRecues(Utilisateur utilisateur) {
        return relationDAO.demandesRecues(utilisateur);
    }

    public List<Utilisateur> bloques(Utilisateur utilisateur) {
        return relationDAO.listerBloquesPar(utilisateur);
    }

    public List<Utilisateur> rechercherUtilisateurs(String texte) {
        if (texte == null || texte.trim().isEmpty()) {
            return List.of();
        }
        return utilisateurDAO.rechercher(texte);
    }

    /** Envoie une demande d'ami. */
    public RelationUtilisateur envoyerDemande(Utilisateur emetteur, Utilisateur cible) {
        if (Objects.equals(emetteur.getId(), cible.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même.");
        }

        RelationUtilisateur existante = relationDAO.trouverEntre(emetteur, cible);
        if (existante != null) {
            switch (existante.getStatut()) {
                case ACCEPTEE:
                    throw new IllegalArgumentException("Vous êtes déjà amis.");
                case EN_ATTENTE:
                    throw new IllegalArgumentException("Une demande est déjà en attente.");
                case BLOQUEE:
                    throw new IllegalArgumentException("Relation bloquée : impossible d'envoyer une demande.");
                default:
                    // REJETEE : une nouvelle demande est autorisée
                    relationDAO.supprimerParId(existante.getId());
            }
        }

        RelationUtilisateur relation = new RelationUtilisateur(emetteur, cible, StatutRelation.EN_ATTENTE);
        relationDAO.sauver(relation);
        return relation;
    }

    public void accepter(RelationUtilisateur relation) {
        relation.setStatut(StatutRelation.ACCEPTEE);
        relationDAO.modifier(relation);
    }

    public void refuser(RelationUtilisateur relation) {
        relation.setStatut(StatutRelation.REJETEE);
        relationDAO.modifier(relation);
    }

    /** Supprime la relation (ami ou demande) entre deux utilisateurs. */
    public void supprimer(Utilisateur a, Utilisateur b) {
        RelationUtilisateur relation = relationDAO.trouverEntre(a, b);
        if (relation != null) {
            relationDAO.supprimerParId(relation.getId());
        }
    }

    /** Bloque un utilisateur (remplace une éventuelle relation existante). */
    public void bloquer(Utilisateur utilisateur, Utilisateur cible) {
        if (Objects.equals(utilisateur.getId(), cible.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous bloquer vous-même.");
        }
        RelationUtilisateur existante = relationDAO.trouverEntre(utilisateur, cible);
        if (existante != null) {
            relationDAO.supprimerParId(existante.getId());
        }
        relationDAO.sauver(new RelationUtilisateur(utilisateur, cible, StatutRelation.BLOQUEE));
    }

    /** Débloque un utilisateur (supprime le blocage). */
    public void debloquer(Utilisateur utilisateur, Utilisateur cible) {
        RelationUtilisateur existante = relationDAO.trouverEntre(utilisateur, cible);
        if (existante != null && existante.getStatut() == StatutRelation.BLOQUEE) {
            relationDAO.supprimerParId(existante.getId());
        }
    }

    public StatutRelation statutEntre(Utilisateur a, Utilisateur b) {
        RelationUtilisateur relation = relationDAO.trouverEntre(a, b);
        return relation == null ? null : relation.getStatut();
    }
}
