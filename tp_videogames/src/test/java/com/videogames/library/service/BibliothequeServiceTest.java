package com.videogames.library.service;

import com.videogames.library.dao.JeuDAO;
import com.videogames.library.model.EntreeBibliotheque;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import com.videogames.library.model.Utilisateur;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BibliothequeServiceTest {

    static {
        System.setProperty("hibernate.config", "hibernate.cfg.test.xml");
    }

    private final AuthService authService = new AuthService();
    private final BibliothequeService bibliotheque = new BibliothequeService();
    private final JeuDAO jeuDAO = new JeuDAO();

    private Utilisateur utilisateur() {
        return authService.inscrire(
                "biblio-" + UUID.randomUUID() + "@test.fr",
                "pseudo-" + UUID.randomUUID(),
                "secret123");
    }

    private Jeu jeu() {
        Jeu jeu = new Jeu();
        jeu.setTitre("Jeu-" + UUID.randomUUID());
        jeu.setMode(ModeJeu.SOLO);
        jeu.setPrix(BigDecimal.TEN);
        jeuDAO.sauver(jeu);
        return jeu;
    }

    @Test
    void ajouter_rend_le_jeu_possede() {
        Utilisateur u = utilisateur();
        Jeu j = jeu();

        EntreeBibliotheque entree = bibliotheque.ajouter(u, j);

        assertTrue(entree.isPossede());
        assertFalse(entree.isAAcheter());
        assertEquals(1, bibliotheque.lister(u).size());
    }

    @Test
    void ajouter_deux_fois_ne_cree_pas_de_doublon() {
        Utilisateur u = utilisateur();
        Jeu j = jeu();

        bibliotheque.ajouter(u, j);
        bibliotheque.ajouter(u, j);

        assertEquals(1, bibliotheque.lister(u).size());
    }

    @Test
    void retirer_supprime_l_entree() {
        Utilisateur u = utilisateur();
        Jeu j = jeu();
        bibliotheque.ajouter(u, j);

        bibliotheque.retirer(u, j);

        assertNull(bibliotheque.entree(u, j));
        assertTrue(bibliotheque.lister(u).isEmpty());
    }

    @Test
    void marquer_a_acheter_retire_le_statut_possede() {
        Utilisateur u = utilisateur();
        Jeu j = jeu();
        bibliotheque.ajouter(u, j);

        EntreeBibliotheque entree = bibliotheque.marquerAAcheter(u, j);

        assertTrue(entree.isAAcheter());
        assertFalse(entree.isPossede());
    }

    @Test
    void basculer_favori_change_le_statut() {
        Utilisateur u = utilisateur();
        Jeu j = jeu();
        bibliotheque.ajouter(u, j);

        assertTrue(bibliotheque.basculerFavori(u, j).isFavori());
        assertFalse(bibliotheque.basculerFavori(u, j).isFavori());
    }

    @Test
    void modifier_possede_annule_a_acheter() {
        Utilisateur u = utilisateur();
        Jeu j = jeu();
        bibliotheque.marquerAAcheter(u, j);

        EntreeBibliotheque entree = bibliotheque.modifier(u, j, true, true, true);

        assertTrue(entree.isPossede());
        assertFalse(entree.isAAcheter());
        assertTrue(entree.isFavori());
    }
}
