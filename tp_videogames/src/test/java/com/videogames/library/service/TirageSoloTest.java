package com.videogames.library.service;

import com.videogames.library.dao.JeuDAO;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import com.videogames.library.model.ResultatTirage;
import com.videogames.library.model.Utilisateur;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TirageSoloTest {

    static {
        System.setProperty("hibernate.config", "hibernate.cfg.test.xml");
    }

    private final AuthService authService = new AuthService();
    private final BibliothequeService bibliotheque = new BibliothequeService();
    private final JeuDAO jeuDAO = new JeuDAO();
    private final TirageService tirageService = new TirageService();

    private Utilisateur utilisateur() {
        return authService.inscrire(
                "solo-" + UUID.randomUUID() + "@test.fr",
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

    @Test
    void tirage_solo_ignore_les_jeux_strictement_online() {
        Utilisateur moi = utilisateur();
        Jeu solo = jeu(ModeJeu.SOLO, "10");
        Jeu enLigne = jeu(ModeJeu.ONLINE, "10");
        bibliotheque.ajouter(moi, solo);
        bibliotheque.ajouter(moi, enLigne);

        ResultatTirage resultat = tirageService.tirerSolo(
                moi, new FiltreTirage(), List.of(), List.of());

        assertNotNull(resultat);
        assertEquals(solo.getTitre(), resultat.getJeu().getTitre());
        assertFalse(resultat.isHorsBibliothequeCommune());
    }

    @Test
    void tirage_solo_ne_prend_que_les_jeux_de_l_utilisateur() {
        Utilisateur moi = utilisateur();
        Utilisateur autre = utilisateur();
        Jeu monJeu = jeu(ModeJeu.SOLO, "10");
        Jeu jeuAutre = jeu(ModeJeu.SOLO, "10");
        bibliotheque.ajouter(moi, monJeu);
        bibliotheque.ajouter(autre, jeuAutre);

        ResultatTirage resultat = tirageService.tirerSolo(
                moi, new FiltreTirage(), List.of(), List.of());

        assertEquals(monJeu.getTitre(), resultat.getJeu().getTitre());
    }

    @Test
    void tirage_solo_accepte_un_ajout_hors_bibliotheque() {
        Utilisateur moi = utilisateur();
        Jeu ajout = jeu(ModeJeu.SOLO, "24.99");

        ResultatTirage resultat = tirageService.tirerSolo(
                moi, new FiltreTirage(), List.of(), List.of(ajout));

        assertTrue(resultat.isHorsBibliothequeCommune());
        assertEquals(ajout.getTitre(), resultat.getJeu().getTitre());
        assertEquals(new BigDecimal("24.99"), resultat.getJeu().getPrix());
    }

    @Test
    void tirage_solo_respecte_les_exclusions() {
        Utilisateur moi = utilisateur();
        Jeu garde = jeu(ModeJeu.SOLO, "10");
        Jeu exclu = jeu(ModeJeu.SOLO, "10");
        bibliotheque.ajouter(moi, garde);
        bibliotheque.ajouter(moi, exclu);

        ResultatTirage resultat = tirageService.tirerSolo(
                moi, new FiltreTirage(), List.of(exclu), List.of());

        assertEquals(garde.getTitre(), resultat.getJeu().getTitre());
    }

    @Test
    void tirage_solo_sans_jeu_retourne_null() {
        Utilisateur moi = utilisateur();

        assertNull(tirageService.tirerSolo(moi, new FiltreTirage(), List.of(), List.of()));
    }
}
