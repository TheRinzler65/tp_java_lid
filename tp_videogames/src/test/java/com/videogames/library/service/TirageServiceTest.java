package com.videogames.library.service;

import com.videogames.library.dao.GenreDAO;
import com.videogames.library.dao.JeuDAO;
import com.videogames.library.dao.PlateformeDAO;
import com.videogames.library.model.Genre;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import com.videogames.library.model.Plateforme;
import com.videogames.library.model.ResultatTirage;
import com.videogames.library.model.Utilisateur;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TirageServiceTest {

    static {
        System.setProperty("hibernate.config", "hibernate.cfg.test.xml");
    }

    private final AuthService authService = new AuthService();
    private final BibliothequeService bibliotheque = new BibliothequeService();
    private final JeuDAO jeuDAO = new JeuDAO();
    private final GenreDAO genreDAO = new GenreDAO();
    private final PlateformeDAO plateformeDAO = new PlateformeDAO();
    private final TirageService tirageService = new TirageService();

    private Utilisateur utilisateur() {
        return authService.inscrire(
                "tirage-" + UUID.randomUUID() + "@test.fr",
                "pseudo-" + UUID.randomUUID(),
                "secret123");
    }

    private Jeu jeu(ModeJeu mode, String prix) {
        Jeu jeu = new Jeu();
        jeu.setTitre("Jeu-" + UUID.randomUUID());
        jeu.setMode(mode);
        jeu.setPrix(new BigDecimal(prix));
        jeuDAO.sauver(jeu);
        return jeu;
    }

    private void posseder(Utilisateur u, Jeu jeu) {
        bibliotheque.ajouter(u, jeu);
    }

    @Test
    void tirage_multi_ignore_les_jeux_strictement_solo() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        Jeu solo = jeu(ModeJeu.SOLO, "10");
        Jeu multi = jeu(ModeJeu.SOLO_ET_ONLINE, "60");
        posseder(a, solo);
        posseder(b, solo);
        posseder(a, multi);
        posseder(b, multi);

        ResultatTirage resultat = tirageService.tirerPourGroupe(
                List.of(a, b), new FiltreTirage(), List.of(), List.of());

        assertNotNull(resultat);
        assertEquals(multi.getTitre(), resultat.getJeu().getTitre());
        assertFalse(resultat.isHorsBibliothequeCommune());
    }

    @Test
    void les_exclusions_sont_respectees() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        Jeu garde = jeu(ModeJeu.ONLINE, "10");
        Jeu exclu = jeu(ModeJeu.ONLINE, "10");
        posseder(a, garde);
        posseder(b, garde);
        posseder(a, exclu);
        posseder(b, exclu);

        ResultatTirage resultat = tirageService.tirerPourGroupe(
                List.of(a, b), new FiltreTirage(), List.of(exclu), List.of());

        assertEquals(garde.getTitre(), resultat.getJeu().getTitre());
    }

    @Test
    void un_jeu_ajoute_hors_bibliotheque_est_signale_avec_son_prix() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        Jeu ajout = jeu(ModeJeu.SOLO_ET_ONLINE, "39.99");

        ResultatTirage resultat = tirageService.tirerPourGroupe(
                List.of(a, b), new FiltreTirage(), List.of(), List.of(ajout));

        assertNotNull(resultat);
        assertTrue(resultat.isHorsBibliothequeCommune());
        assertEquals(ajout.getTitre(), resultat.getJeu().getTitre());
        assertEquals(new BigDecimal("39.99"), resultat.getJeu().getPrix());
    }

    @Test
    void filtre_par_genre() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        Genre rpg = genreDAO.trouverOuCreer("RPG-" + UUID.randomUUID());
        Genre sport = genreDAO.trouverOuCreer("Sport-" + UUID.randomUUID());

        Jeu jeuRpg = jeu(ModeJeu.ONLINE, "10");
        jeuRpg.getGenres().add(rpg);
        jeuDAO.modifier(jeuRpg);
        Jeu jeuSport = jeu(ModeJeu.ONLINE, "10");
        jeuSport.getGenres().add(sport);
        jeuDAO.modifier(jeuSport);

        posseder(a, jeuRpg);
        posseder(b, jeuRpg);
        posseder(a, jeuSport);
        posseder(b, jeuSport);

        FiltreTirage filtre = new FiltreTirage();
        filtre.ajouterGenre(rpg.getId());

        ResultatTirage resultat = tirageService.tirerPourGroupe(
                List.of(a, b), filtre, List.of(), List.of());

        assertEquals(jeuRpg.getTitre(), resultat.getJeu().getTitre());
    }

    @Test
    void filtre_par_plateforme() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        Plateforme pc = plateformeDAO.trouverOuCreer("PC-" + UUID.randomUUID());
        Plateforme switchPlateforme = plateformeDAO.trouverOuCreer("Switch-" + UUID.randomUUID());

        Jeu jeuPc = jeu(ModeJeu.ONLINE, "10");
        jeuPc.getPlateformes().add(pc);
        jeuDAO.modifier(jeuPc);
        Jeu jeuSwitch = jeu(ModeJeu.ONLINE, "10");
        jeuSwitch.getPlateformes().add(switchPlateforme);
        jeuDAO.modifier(jeuSwitch);

        posseder(a, jeuPc);
        posseder(b, jeuPc);
        posseder(a, jeuSwitch);
        posseder(b, jeuSwitch);

        FiltreTirage filtre = new FiltreTirage();
        filtre.ajouterPlateforme(switchPlateforme.getId());

        ResultatTirage resultat = tirageService.tirerPourGroupe(
                List.of(a, b), filtre, List.of(), List.of());

        assertEquals(jeuSwitch.getTitre(), resultat.getJeu().getTitre());
    }

    @Test
    void filtre_par_prix_maximum() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        Jeu pasCher = jeu(ModeJeu.ONLINE, "10");
        Jeu cher = jeu(ModeJeu.ONLINE, "80");
        posseder(a, pasCher);
        posseder(b, pasCher);
        posseder(a, cher);
        posseder(b, cher);

        FiltreTirage filtre = new FiltreTirage();
        filtre.setPrixMax(new BigDecimal("40"));

        ResultatTirage resultat = tirageService.tirerPourGroupe(
                List.of(a, b), filtre, List.of(), List.of());

        assertEquals(pasCher.getTitre(), resultat.getJeu().getTitre());
    }

    @Test
    void aucun_candidat_retourne_null() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();

        ResultatTirage resultat = tirageService.tirerPourGroupe(
                List.of(a, b), new FiltreTirage(), List.of(), List.of());

        assertNull(resultat);
    }
}
