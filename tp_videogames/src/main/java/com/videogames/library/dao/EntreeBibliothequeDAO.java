package com.videogames.library.dao;

import com.videogames.library.model.EntreeBibliotheque;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.Utilisateur;
import com.videogames.library.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class EntreeBibliothequeDAO extends AbstractDAO<EntreeBibliotheque> {

    public EntreeBibliothequeDAO() {
        super(EntreeBibliotheque.class);
    }

    /**
     * Clé composite : persist() échouerait sur une ligne déjà existante,
     * on passe donc systématiquement par merge (insert ou update).
     */
    @Override
    public void sauver(EntreeBibliotheque objet) {
        modifier(objet);
    }

    /** Entrée (utilisateur, jeu) de la bibliothèque, ou null. */
    public EntreeBibliotheque trouver(Utilisateur u, Jeu jeu) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            EntreeBibliotheque entree = session.createQuery(
                    "select e from EntreeBibliotheque e join fetch e.jeu "
                            + "where e.utilisateur = :u and e.jeu = :j",
                    EntreeBibliotheque.class)
                    .setParameter("u", u)
                    .setParameter("j", jeu)
                    .uniqueResult();
            if (entree != null) {
                Hibernate.initialize(entree.getJeu().getGenres());
                Hibernate.initialize(entree.getJeu().getPlateformes());
            }
            return entree;
        }
    }

    /** Supprime l'entrée (utilisateur, jeu) si elle existe. */
    public void supprimer(Utilisateur u, Jeu jeu) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.createMutationQuery(
                    "delete from EntreeBibliotheque e "
                            + "where e.utilisateur = :u and e.jeu = :j")
                    .setParameter("u", u)
                    .setParameter("j", jeu)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    /** Entrées de la bibliothèque d'un utilisateur (avec le jeu et ses collections chargés). */
    public List<EntreeBibliotheque> listerJeuxDe(Utilisateur u) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<EntreeBibliotheque> entries = session.createQuery(
                    "select e from EntreeBibliotheque e "
                            + "left join fetch e.jeu j "
                            + "where e.utilisateur = :u",
                    EntreeBibliotheque.class)
                    .setParameter("u", u)
                    .getResultList();
            // initialise les collections lazy du jeu pendant que la session est ouverte
            for (EntreeBibliotheque e : entries) {
                Hibernate.initialize(e.getJeu().getGenres());
                Hibernate.initialize(e.getJeu().getPlateformes());
            }
            return entries;
        }
    }

    /**
     * Jeux possédés par TOUS les utilisateurs passés en paramètre.
     *
     * @param ids        identifiants des membres du groupe (1 minimum)
     * @param nbMembres  taille du groupe
     */
    public List<Jeu> intersection(List<Long> ids, int nbMembres) {
        if (ids == null || ids.isEmpty() || nbMembres <= 0) {
            return new ArrayList<>();
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Jeu> jeux = session.createQuery(
                    "select j from Jeu j where j.id in ("
                            + "  select e.jeu.id from EntreeBibliotheque e "
                            + "  where e.possede = true and e.utilisateur.id in (:ids) "
                            + "  group by e.jeu.id "
                            + "  having count(distinct e.utilisateur.id) = :n)",
                    Jeu.class)
                    .setParameter("ids", ids)
                    .setParameter("n", nbMembres)
                    .getResultList();
            for (Jeu j : jeux) {
                Hibernate.initialize(j.getGenres());
                Hibernate.initialize(j.getPlateformes());
            }
            return jeux;
        }
    }

    /** Jeux possédés par un utilisateur précis (raccourci de listerJeuxDe). */
    public List<Jeu> jeuxPossedesDe(Utilisateur u) {
        List<Jeu> jeux = new ArrayList<>();
        for (EntreeBibliotheque e : listerJeuxDe(u)) {
            if (e.isPossede()) {
                jeux.add(e.getJeu());
            }
        }
        return jeux;
    }
}
