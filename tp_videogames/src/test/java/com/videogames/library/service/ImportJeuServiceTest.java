package com.videogames.library.service;

import com.videogames.library.api.JeuIgdb;
import com.videogames.library.model.Jeu;
import com.videogames.library.model.ModeJeu;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ImportJeuServiceTest {

    static {
        System.setProperty("hibernate.config", "hibernate.cfg.test.xml");
    }

    private final ImportJeuService importService = new ImportJeuService();

    private JeuIgdb source(String titre) {
        return new JeuIgdb(1L, titre, "Description du jeu", LocalDate.of(2020, 1, 1),
                "https://example.com/cover.jpg", ModeJeu.SOLO_ET_ONLINE,
                List.of("Action-" + UUID.randomUUID()), List.of("PC-" + UUID.randomUUID()));
    }

    @Test
    void importe_un_jeu_avec_genres_et_plateformes() {
        Jeu jeu = importService.importer(source("Titre-" + UUID.randomUUID()));

        assertNotNull(jeu.getId());
        assertNotNull(jeu.getMode());
        assertEquals(1, jeu.getGenres().size());
        assertEquals(1, jeu.getPlateformes().size());
        assertEquals("https://example.com/cover.jpg", jeu.getUrlImage());
    }

    @Test
    void un_second_import_du_meme_titre_ne_cree_pas_de_doublon() {
        String titre = "Titre-" + UUID.randomUUID();
        Jeu premier = importService.importer(source(titre));
        Jeu second = importService.importer(source(titre));

        assertEquals(premier.getId(), second.getId());
    }
}
