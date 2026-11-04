package com.videogames.library.service;

import com.videogames.library.model.Utilisateur;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    static {
        System.setProperty("hibernate.config", "hibernate.cfg.test.xml");
    }

    private final AuthService authService = new AuthService();

    private static String emailUnique() {
        return "user-" + UUID.randomUUID() + "@test.fr";
    }

    @Test
    void inscription_hache_le_mot_de_passe() {
        String email = emailUnique();
        Utilisateur u = authService.inscrire(email, "pseudo-" + UUID.randomUUID(), "secret123");

        assertNotNull(u.getId());
        assertEquals(email, u.getEmail());
        assertNotEquals("secret123", u.getMdp());
        assertTrue(u.getMdp().startsWith("$2"));
    }

    @Test
    void inscription_refuse_un_email_deja_utilise() {
        String email = emailUnique();
        authService.inscrire(email, "pseudo-" + UUID.randomUUID(), "secret123");

        assertThrows(IllegalArgumentException.class,
                () -> authService.inscrire(email, "autre-" + UUID.randomUUID(), "secret123"));
    }

    @Test
    void inscription_refuse_un_email_invalide() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.inscrire("pas-un-email", "pseudo-" + UUID.randomUUID(), "secret123"));
    }

    @Test
    void connexion_accepte_les_bons_identifiants() {
        String email = emailUnique();
        authService.inscrire(email, "pseudo-" + UUID.randomUUID(), "motdepasse");

        Utilisateur connecte = authService.connecter(email, "motdepasse");

        assertNotNull(connecte);
        assertEquals(email, connecte.getEmail());
    }

    @Test
    void connexion_refuse_un_mauvais_mot_de_passe() {
        String email = emailUnique();
        authService.inscrire(email, "pseudo-" + UUID.randomUUID(), "motdepasse");

        assertNull(authService.connecter(email, "mauvais"));
    }

    @Test
    void connexion_refuse_un_email_inconnu() {
        assertNull(authService.connecter("inconnu-" + UUID.randomUUID() + "@test.fr", "secret123"));
    }
}
