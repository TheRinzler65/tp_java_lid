package com.videogames.library.dao;

import com.videogames.library.model.Genre;
import com.videogames.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class GenreDAO extends AbstractDAO<Genre> {

    public GenreDAO() {
        super(Genre.class);
    }

    /** Retourne le genre portant ce nom (insensible à la casse), le crée s'il n'existe pas. */
    public Genre trouverOuCreer(String nom) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Genre genre = session.createQuery("from Genre g where lower(g.nom) = :n", Genre.class)
                    .setParameter("n", nom.toLowerCase())
                    .uniqueResult();
            if (genre == null) {
                genre = new Genre(nom);
                session.persist(genre);
            }
            tx.commit();
            return genre;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }
}
