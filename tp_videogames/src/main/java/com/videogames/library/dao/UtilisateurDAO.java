package com.videogames.library.dao;

import com.videogames.library.model.Utilisateur;
import com.videogames.library.util.HibernateUtil;
import org.hibernate.Session;

public class UtilisateurDAO extends AbstractDAO<Utilisateur> {

    public UtilisateurDAO() {
        super(Utilisateur.class);
    }

    public Utilisateur trouverParEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Utilisateur u where u.email = :email", Utilisateur.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    public Utilisateur trouverParPseudo(String pseudo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Utilisateur u where u.pseudo = :pseudo", Utilisateur.class)
                    .setParameter("pseudo", pseudo)
                    .uniqueResult();
        }
    }

    /** Recherche les utilisateurs dont le pseudo ou l'email contient le texte. */
    public java.util.List<Utilisateur> rechercher(String texte) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from Utilisateur u "
                            + "where lower(u.pseudo) like :texte or lower(u.email) like :texte "
                            + "order by u.pseudo",
                    Utilisateur.class)
                    .setParameter("texte", "%" + texte.trim().toLowerCase() + "%")
                    .getResultList();
        }
    }
}