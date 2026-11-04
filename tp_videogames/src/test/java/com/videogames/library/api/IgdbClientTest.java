package com.videogames.library.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.videogames.library.model.ModeJeu;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IgdbClientTest {

    private final IgdbClient client = new IgdbClient("client-id", "client-secret");

    private static final String JSON = "[{"
            + "\"id\":123,"
            + "\"name\":\"The Witcher 3\","
            + "\"summary\":\"Un RPG en monde ouvert.\","
            + "\"first_release_date\":1431993600,"
            + "\"cover\":{\"url\":\"//images.igdb.com/igdb/image/upload/t_cover_big/co1.jpg\"},"
            + "\"genres\":[{\"name\":\"Role-playing (RPG)\"},{\"name\":\"Adventure\"}],"
            + "\"platforms\":[{\"name\":\"PC (Microsoft Windows)\"},{\"name\":\"PlayStation 4\"}],"
            + "\"game_modes\":[{\"id\":1},{\"id\":2}]"
            + "}]";

    private static ArrayNode modes(int... ids) {
        ArrayNode tableau = new ObjectMapper().createArrayNode();
        for (int id : ids) {
            tableau.addObject().put("id", id);
        }
        return tableau;
    }

    @Test
    void parse_mappe_tous_les_champs() throws Exception {
        List<JeuIgdb> jeux = client.parser(JSON);

        assertEquals(1, jeux.size());
        JeuIgdb jeu = jeux.get(0);
        assertEquals(123L, jeu.getIgdbId());
        assertEquals("The Witcher 3", jeu.getTitre());
        assertEquals("Un RPG en monde ouvert.", jeu.getDescription());
        assertEquals(LocalDate.of(2015, 5, 19), jeu.getDateSortie());
        assertEquals("https://images.igdb.com/igdb/image/upload/t_cover_big/co1.jpg", jeu.getUrlImage());
        assertEquals(ModeJeu.SOLO_ET_ONLINE, jeu.getMode());
        assertEquals(List.of("Role-playing (RPG)", "Adventure"), jeu.getGenres());
        assertEquals(List.of("PC (Microsoft Windows)", "PlayStation 4"), jeu.getPlateformes());
    }

    @Test
    void mode_solo_uniquement() {
        assertEquals(ModeJeu.SOLO, IgdbClient.mode(modes(1)));
    }

    @Test
    void mode_multi_uniquement() {
        assertEquals(ModeJeu.ONLINE, IgdbClient.mode(modes(2)));
    }

    @Test
    void mode_solo_et_multi() {
        assertEquals(ModeJeu.SOLO_ET_ONLINE, IgdbClient.mode(modes(1, 3)));
    }

    @Test
    void non_configure_si_identifiants_absents() {
        assertFalse(new IgdbClient(null, null).estConfigure());
        assertTrue(client.estConfigure());
    }
}
