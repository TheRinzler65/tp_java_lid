package com.videogames.library.util;

import com.videogames.library.dao.GenreDAO;
import com.videogames.library.dao.JeuDAO;
import com.videogames.library.dao.PlateformeDAO;
import com.videogames.library.model.Genre;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import com.videogames.library.model.Plateforme;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

public final class DataInitializer {

    private DataInitializer() {
    }

    private static String couverture(int appId) {
        return "https://cdn.cloudflare.steamstatic.com/steam/apps/" + appId + "/header.jpg";
    }

    public static void initialiser() {
        JeuDAO jeuDAO = new JeuDAO();
        if (!jeuDAO.trouverTous().isEmpty()) {
            return;
        }

        GenreDAO genreDAO = new GenreDAO();
        PlateformeDAO plateformeDAO = new PlateformeDAO();

        String[] genres = {
                "Action", "Aventure", "RPG", "FPS", "Plateforme", "Stratégie",
                "Simulation", "Sport", "Course", "Combat", "Horreur", "Indie",
                "Battle Royale", "Metroidvania", "Roguelike", "Sandbox", "MOBA",
                "Tactique", "Coopération", "Monde ouvert"
        };
        for (String g : genres) {
            genreDAO.trouverOuCreer(g);
        }

        String[] plateformes = {
                "PC", "PlayStation 5", "PlayStation 4", "Xbox Series X|S", "Xbox One",
                "Nintendo Switch", "Steam Deck", "PlayStation 3", "Xbox 360", "Wii"
        };
        for (String p : plateformes) {
            plateformeDAO.trouverOuCreer(p);
        }

        int nb = 0;
        nb += ajouter(jeuDAO, "The Witcher 3: Wild Hunt",
                "Chasseur de monstres Geralt de Rivia traverse un monde ouvert sombre et riche en quêtes.",
                "2015-05-19", 39.99, couverture(292030), ModeJeu.SOLO,
                new String[]{"RPG", "Aventure", "Monde ouvert"},
                new String[]{"PC", "PlayStation 5", "PlayStation 4", "Xbox Series X|S", "Xbox One", "Nintendo Switch", "Steam Deck"});

        nb += ajouter(jeuDAO, "Elden Ring",
                "Action-RPG en monde ouvert signé FromSoftware, récompensé du Game de l'année 2022.",
                "2022-02-25", 59.99, couverture(1245620), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"Action", "RPG", "Monde ouvert"},
                new String[]{"PC", "PlayStation 5", "PlayStation 4", "Xbox Series X|S", "Xbox One", "Steam Deck"});

        nb += ajouter(jeuDAO, "Cyberpunk 2077",
                "RPG futuriste dans la mégapole de Night City, vu à la première personne.",
                "2020-12-10", 59.99, couverture(1091500), ModeJeu.SOLO,
                new String[]{"Action", "RPG", "Aventure"},
                new String[]{"PC", "PlayStation 5", "PlayStation 4", "Xbox Series X|S", "Xbox One", "Steam Deck"});

        nb += ajouter(jeuDAO, "Baldur's Gate 3",
                "RPG tactique au tour par tour dans l'univers de Dungeons & Dragons, jouable à 4 en coopération.",
                "2023-08-03", 59.99, couverture(1086940), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"RPG", "Stratégie", "Aventure"},
                new String[]{"PC", "PlayStation 5", "Xbox Series X|S", "Steam Deck"});

        nb += ajouter(jeuDAO, "Counter-Strike 2",
                "Le FPS compétitif de référence, gratuit et joué en équipes de 5.",
                "2012-08-21", 0.00, couverture(730), ModeJeu.ONLINE,
                new String[]{"FPS", "Action", "Tactique"},
                new String[]{"PC", "Steam Deck"});

        nb += ajouter(jeuDAO, "Dota 2",
                "MOBA stratégique en 5 contre 5, gratuit et mondialement connu pour son e-sport.",
                "2012-07-09", 0.00, couverture(570), ModeJeu.ONLINE,
                new String[]{"MOBA", "Stratégie", "Action"},
                new String[]{"PC", "Steam Deck"});

        nb += ajouter(jeuDAO, "Apex Legends",
                "Battle Royale en équipes de 3 avec des héros aux compétences uniques, gratuit.",
                "2020-11-04", 0.00, couverture(1172470), ModeJeu.ONLINE,
                new String[]{"Battle Royale", "FPS", "Action"},
                new String[]{"PC", "PlayStation 5", "PlayStation 4", "Xbox Series X|S", "Xbox One", "Nintendo Switch", "Steam Deck"});

        nb += ajouter(jeuDAO, "Rocket League",
                "Du football… avec des voitures-fusées. Arène multijoueur compétitive et déjantée.",
                "2015-07-07", 19.99, couverture(252950), ModeJeu.ONLINE,
                new String[]{"Sport", "Course", "Coopération"},
                new String[]{"PC", "PlayStation 5", "PlayStation 4", "Xbox Series X|S", "Xbox One", "Nintendo Switch", "Steam Deck"});

        nb += ajouter(jeuDAO, "It Takes Two",
                "Plateforme-coopération pensé pour deux joueurs : à jouer absolument à deux, en ligne ou sur le canapé.",
                "2021-03-26", 39.99, couverture(1426210), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"Aventure", "Plateforme", "Coopération"},
                new String[]{"PC", "PlayStation 5", "PlayStation 4", "Xbox Series X|S", "Xbox One", "Nintendo Switch", "Steam Deck"});

        nb += ajouter(jeuDAO, "Stardew Valley",
                "Simulateur d'agriculture détendu : culture, élevage, pêche et vie du village.",
                "2016-02-26", 14.99, couverture(413150), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"Simulation", "RPG", "Indie"},
                new String[]{"PC", "PlayStation 4", "Xbox One", "Nintendo Switch", "Steam Deck"});

        nb += ajouter(jeuDAO, "Hollow Knight",
                "Metroidvania exigeant dans le royaume insecte d'Hollownest, magnifiquement dessiné.",
                "2017-02-24", 14.99, couverture(367520), ModeJeu.SOLO,
                new String[]{"Metroidvania", "Plateforme", "Action", "Indie"},
                new String[]{"PC", "PlayStation 4", "Xbox One", "Nintendo Switch", "Steam Deck"});

        nb += ajouter(jeuDAO, "Hades",
                "Roguelike dynamique où chaque mort vous rapproche de l'Olympe. Excellente rejouabilité.",
                "2020-09-17", 24.99, couverture(1145360), ModeJeu.SOLO,
                new String[]{"Roguelike", "Action", "Indie"},
                new String[]{"PC", "PlayStation 5", "Xbox Series X|S", "Nintendo Switch", "Steam Deck"});

        nb += ajouter(jeuDAO, "Forza Horizon 5",
                "Course en monde ouvert au Mexique, plus de 500 voitures et un multijoueur complet.",
                "2021-11-09", 59.99, couverture(1551360), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"Course", "Monde ouvert", "Simulation"},
                new String[]{"PC", "Xbox Series X|S", "Xbox One", "Steam Deck"});

        nb += ajouter(jeuDAO, "Terraria",
                "Sandbox 2D de construction et d'exploration, jouable en solo ou en équipe.",
                "2011-05-16", 9.99, couverture(105600), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"Sandbox", "Aventure", "Indie"},
                new String[]{"PC", "PlayStation 4", "Xbox One", "Nintendo Switch", "Steam Deck"});

        nb += ajouter(jeuDAO, "Red Dead Redemption 2",
                "Épopée de hors-la-loi dans l'Ouest américain, solo et mode en ligne.",
                "2018-10-26", 59.99, couverture(1174180), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"Action", "Aventure", "Monde ouvert"},
                new String[]{"PC", "PlayStation 4", "Xbox One"});

        nb += ajouter(jeuDAO, "Grand Theft Auto V",
                "Liberté totale dans Los Santos, campagne solo et GTA Online.",
                "2015-04-14", 29.99, couverture(271590), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"Action", "Aventure", "Monde ouvert"},
                new String[]{"PC", "PlayStation 5", "PlayStation 4", "Xbox Series X|S", "Xbox One", "Steam Deck"});

        nb += ajouter(jeuDAO, "Overcooked! 2",
                "Cuisine en panique à 4 joueurs : le jeu coopératif parfait pour les soirées.",
                "2018-08-07", 19.99, couverture(728880), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"Simulation", "Coopération", "Indie"},
                new String[]{"PC", "PlayStation 4", "Xbox One", "Nintendo Switch"});

        nb += ajouter(jeuDAO, "Resident Evil 4",
                "Survival horror d'action revisité : tension, munitions comptées et mise en scène.",
                "2023-03-24", 59.99, couverture(2050650), ModeJeu.SOLO,
                new String[]{"Horreur", "Action", "Aventure"},
                new String[]{"PC", "PlayStation 5", "Xbox Series X|S", "Steam Deck"});

        nb += ajouter(jeuDAO, "PUBG: BATTLEGROUNDS",
                "Le battle royale qui a lancé le genre : 100 joueurs, une seule équipe survivante.",
                "2017-12-21", 0.00, couverture(578080), ModeJeu.ONLINE,
                new String[]{"Battle Royale", "FPS", "Action"},
                new String[]{"PC", "PlayStation 5", "Xbox Series X|S"});

        nb += ajouter(jeuDAO, "Left 4 Dead 2",
                "Coopération horrifique contre des hordes d'infestés, 4 survivants ensemble.",
                "2009-11-17", 9.99, couverture(550), ModeJeu.SOLO_ET_ONLINE,
                new String[]{"FPS", "Horreur", "Coopération"},
                new String[]{"PC", "Xbox 360", "Steam Deck"});

        System.out.println("[DataInitializer] " + nb + " jeux, "
                + genres.length + " genres et " + plateformes.length + " plateformes insérés.");
    }

    private static int ajouter(JeuDAO jeuDAO, String titre, String description, String dateSortie,
                               double prix, String image, ModeJeu mode,
                               String[] nomsGenres, String[] nomsPlateformes) {
        GenreDAO genreDAO = new GenreDAO();
        PlateformeDAO plateformeDAO = new PlateformeDAO();

        Jeu jeu = new Jeu();
        jeu.setTitre(titre);
        jeu.setDescription(description);
        jeu.setDateSortie(LocalDate.parse(dateSortie));
        jeu.setPrix(BigDecimal.valueOf(prix));
        jeu.setUrlImage(image);
        jeu.setMode(mode);

        Set<Genre> genres = new LinkedHashSet<>();
        for (String g : nomsGenres) {
            genres.add(genreDAO.trouverOuCreer(g));
        }
        jeu.setGenres(genres);

        Set<Plateforme> plateformes = new LinkedHashSet<>();
        for (String p : nomsPlateformes) {
            plateformes.add(plateformeDAO.trouverOuCreer(p));
        }
        jeu.setPlateformes(plateformes);

        jeuDAO.sauver(jeu);
        return 1;
    }
}
