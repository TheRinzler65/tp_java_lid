package com.videogames.library.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            Dotenv.charger();

            String config = System.getProperty("hibernate.config", "hibernate.cfg.xml");
            Configuration configuration = new Configuration().configure(config);

            if ("hibernate.cfg.xml".equals(config)) {
                appliquer(configuration, "hibernate.connection.url", "DB_URL");
                appliquer(configuration, "hibernate.connection.username", "DB_USER");
                appliquer(configuration, "hibernate.connection.password", "DB_PASSWORD");
            }

            sessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Echec de la creation de la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static void appliquer(Configuration configuration, String propriete, String cle) {
        String valeur = Dotenv.valeur(cle, null);
        if (valeur != null) {
            configuration.setProperty(propriete, valeur);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}
