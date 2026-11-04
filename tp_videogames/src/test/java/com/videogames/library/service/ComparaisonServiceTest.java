package com.videogames.library.service;

import com.videogames.library.dao.JeuDAO;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import com.videogames.library.model.Utilisateur;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ComparaisonServiceTest {

    static {
        System.setProperty("hibernate.config", "hibernate.cfg.test.xml");
    }

    private final AuthService authService = new AuthService();
    private final BibliothequeService bibliotheque = new BibliothequeService();
    private final ComparaisonService comparaison = new ComparaisonService();
    private final JeuDAO jeuDAO = new JeuDAO();

    private Utilisateur utilisateur() {
        return authService.inscrire(
                "cmp-" + UUID.randomUUID() + "@test.fr",
                "pseudo-" + UUID.randomUUID(),
                "secret123");
    }

    private Jeu jeu() {
        Jeu jeu = new Jeu();
        jeu.setTitre("Jeu-" + UUID.randomUUID());
        jeu.setMode(ModeJeu.SOLO);
        jeu.setPrix(BigDecimal.ONE);
        jeuDAO.sauver(jeu);
        return jeu;
    }

    @Test
    void compare_les_jeux_communs_et_exclusifs() {
        Utilisateur moi = utilisateur();
        Utilisateur autre = utilisateur();
        Jeu commun = jeu();
        Jeu seulementMoi = jeu();
        Jeu seulementAutre = jeu();

        bibliotheque.ajouter(moi, commun);
        bibliotheque.ajouter(autre, commun);
        bibliotheque.ajouter(moi, seulementMoi);
        bibliotheque.ajouter(autre, seulementAutre);

        ComparaisonResultat resultat = comparaison.comparer(moi, autre);

        assertEquals(1, resultat.getCommuns().size());
        assertEquals(commun.getTitre(), resultat.getCommuns().get(0).getTitre());
        assertEquals(1, resultat.getSeulementChezMoi().size());
        assertEquals(seulementMoi.getTitre(), resultat.getSeulementChezMoi().get(0).getTitre());
        assertEquals(1, resultat.getSeulementChezAutre().size());
        assertEquals(seulementAutre.getTitre(), resultat.getSeulementChezAutre().get(0).getTitre());
    }

    @Test
    void communs_groupe_ne_retourne_que_les_jeux_de_tous() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        Jeu commun = jeu();
        Jeu pasPourTous = jeu();

        bibliotheque.ajouter(a, commun);
        bibliotheque.ajouter(b, commun);
        bibliotheque.ajouter(a, pasPourTous);

        List<Jeu> resultat = comparaison.communsGroupe(List.of(a, b));

        assertEquals(1, resultat.size());
        assertEquals(commun.getTitre(), resultat.get(0).getTitre());
    }
}
