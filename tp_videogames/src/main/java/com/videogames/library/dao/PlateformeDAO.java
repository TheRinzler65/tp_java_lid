package com.videogames.library.dao;

import com.videogames.library.model.Plateforme;
import com.videogames.library.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class PlateformeDAO extends AbstractDAO<Plateforme> {

    public PlateformeDAO() {
        super(Plateforme.class);
    }

    public Plateforme trouverOuCreer(String nom) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Plateforme plateforme = session
                    .createQuery("from Plateforme p where lower(p.nom) = :n", Plateforme.class)
                    .setParameter("n", nom.toLowerCase())
                    .uniqueResult();
            if (plateforme == null) {
                plateforme = new Plateforme(nom);
                session.persist(plateforme);
            }
            tx.commit();
            return plateforme;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }
}
