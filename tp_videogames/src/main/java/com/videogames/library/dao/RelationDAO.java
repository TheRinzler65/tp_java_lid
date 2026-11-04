package com.videogames.library.dao;

import com.videogames.library.model.RelationUtilisateur;
import com.videogames.library.model.StatutRelation;
import com.videogames.library.model.Utilisateur;
import com.videogames.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class RelationDAO extends AbstractDAO<RelationUtilisateur> {

    public RelationDAO() {
        super(RelationUtilisateur.class);
    }

    public List<RelationUtilisateur> listerRelationsDe(Utilisateur u) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from RelationUtilisateur r "
                            + "left join fetch r.emetteur left join fetch r.cible "
                            + "where r.emetteur = :u or r.cible = :u",
                    RelationUtilisateur.class)
                    .setParameter("u", u)
                    .getResultList();
        }
    }

    public List<RelationUtilisateur> demandesRecues(Utilisateur u) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from RelationUtilisateur r "
                            + "left join fetch r.emetteur left join fetch r.cible "
                            + "where r.cible = :u and r.statut = :st "
                            + "order by r.dateDemande desc",
                    RelationUtilisateur.class)
                    .setParameter("u", u)
                    .setParameter("st", StatutRelation.EN_ATTENTE)
                    .getResultList();
        }
    }

    public RelationUtilisateur trouverEntre(Utilisateur a, Utilisateur b) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from RelationUtilisateur r "
                            + "left join fetch r.emetteur left join fetch r.cible "
                            + "where (r.emetteur = :a and r.cible = :b) "
                            + "or (r.emetteur = :b and r.cible = :a)",
                    RelationUtilisateur.class)
                    .setParameter("a", a)
                    .setParameter("b", b)
                    .getResultList()
                    .stream().findFirst().orElse(null);
        }
    }

    public List<Utilisateur> listerAmisDe(Utilisateur u) {
        List<Utilisateur> amis = new ArrayList<>();
        for (RelationUtilisateur r : listerRelationsDe(u)) {
            if (r.getStatut() == StatutRelation.ACCEPTEE) {
                Utilisateur autre = r.autreQue(u);
                if (autre != null && !autre.getId().equals(u.getId())) {
                    amis.add(autre);
                }
            }
        }
        return amis;
    }

    public List<Utilisateur> listerBloquesPar(Utilisateur u) {
        List<Utilisateur> bloques = new ArrayList<>();
        for (RelationUtilisateur r : listerRelationsDe(u)) {
            if (r.getStatut() == StatutRelation.BLOQUEE
                    && r.getEmetteur() != null
                    && r.getEmetteur().getId().equals(u.getId())
                    && r.getCible() != null) {
                bloques.add(r.getCible());
            }
        }
        return bloques;
    }

    public List<Utilisateur> listerBloquesDe(Utilisateur u) {
        List<Utilisateur> bloqueurs = new ArrayList<>();
        for (RelationUtilisateur r : listerRelationsDe(u)) {
            if (r.getStatut() == StatutRelation.BLOQUEE
                    && r.getCible() != null
                    && r.getCible().getId().equals(u.getId())
                    && r.getEmetteur() != null) {
                bloqueurs.add(r.getEmetteur());
            }
        }
        return bloqueurs;
    }

    public void supprimerParId(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            RelationUtilisateur r = session.get(RelationUtilisateur.class, id);
            if (r != null) {
                session.remove(r);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }
}
