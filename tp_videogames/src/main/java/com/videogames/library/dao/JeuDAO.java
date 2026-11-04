package com.videogames.library.dao;

import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import com.videogames.library.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JeuDAO extends AbstractDAO<Jeu> {

    public JeuDAO() {
        super(Jeu.class);
    }

    @Override
    public List<Jeu> trouverTous() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Jeu> jeux = session.createQuery("from Jeu j", Jeu.class).getResultList();
            return chargerAvecCollections(session, jeux);
        }
    }

    public List<Jeu> rechercherParNom(String nom) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Jeu> jeux = session.createQuery(
                    "from Jeu j where lower(j.titre) like lower(concat('%', :nom, '%'))",
                    Jeu.class)
                    .setParameter("nom", nom)
                    .getResultList();
            return chargerAvecCollections(session, jeux);
        }
    }

    public List<Jeu> rechercher(String titre,
                                Collection<Long> genreIds,
                                Collection<Long> plateformeIds,
                                ModeJeu mode,
                                boolean multiSeulement,
                                BigDecimal prixMax) {
        StringBuilder hql = new StringBuilder("select distinct j from Jeu j");
        if (genreIds != null && !genreIds.isEmpty()) {
            hql.append(" left join j.genres g");
        }
        if (plateformeIds != null && !plateformeIds.isEmpty()) {
            hql.append(" left join j.plateformes p");
        }

        List<String> conditions = new ArrayList<>();
        List<String> nomsParametres = new ArrayList<>();

        if (titre != null && !titre.trim().isEmpty()) {
            conditions.add("lower(j.titre) like :titre");
            nomsParametres.add("titre");
        }
        if (genreIds != null && !genreIds.isEmpty()) {
            conditions.add("g.id in (:genreIds)");
            nomsParametres.add("genreIds");
        }
        if (plateformeIds != null && !plateformeIds.isEmpty()) {
            conditions.add("p.id in (:plateformeIds)");
            nomsParametres.add("plateformeIds");
        }
        if (mode != null) {
            conditions.add("j.mode = :mode");
            nomsParametres.add("mode");
        }
        if (multiSeulement) {
            conditions.add("j.mode <> :solo");
            nomsParametres.add("solo");
        }
        if (prixMax != null) {
            conditions.add("j.prix <= :prixMax");
            nomsParametres.add("prixMax");
        }

        boolean premier = true;
        for (String c : conditions) {
            hql.append(premier ? " where " : " and ").append(c);
            premier = false;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            var query = session.createQuery(hql.toString(), Jeu.class);
            if (nomsParametres.contains("titre")) {
                query.setParameter("titre", "%" + titre.trim().toLowerCase() + "%");
            }
            if (nomsParametres.contains("genreIds")) {
                query.setParameter("genreIds", genreIds);
            }
            if (nomsParametres.contains("plateformeIds")) {
                query.setParameter("plateformeIds", plateformeIds);
            }
            if (nomsParametres.contains("mode")) {
                query.setParameter("mode", mode);
            }
            if (nomsParametres.contains("solo")) {
                query.setParameter("solo", ModeJeu.SOLO);
            }
            if (nomsParametres.contains("prixMax")) {
                query.setParameter("prixMax", prixMax);
            }
            return chargerAvecCollections(session, query.getResultList());
        }
    }

    private List<Jeu> chargerAvecCollections(Session session, List<Jeu> jeux) {
        for (Jeu j : jeux) {
            Hibernate.initialize(j.getGenres());
            Hibernate.initialize(j.getPlateformes());
        }
        return jeux;
    }
}
