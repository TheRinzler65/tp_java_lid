package com.videogames.library.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            // Permet aux tests d'utiliser un autre fichier (ex. hibernate.cfg.test.xml + H2).
            String config = System.getProperty("hibernate.config", "hibernate.cfg.xml");
            sessionFactory = new Configuration().configure(config).buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Echec de la creation de la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}