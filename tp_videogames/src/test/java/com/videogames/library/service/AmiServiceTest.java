package com.videogames.library.service;

import com.videogames.library.model.RelationUtilisateur;
import com.videogames.library.model.StatutRelation;
import com.videogames.library.model.Utilisateur;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AmiServiceTest {

    static {
        System.setProperty("hibernate.config", "hibernate.cfg.test.xml");
    }

    private final AuthService authService = new AuthService();
    private final AmiService amiService = new AmiService();

    private Utilisateur utilisateur() {
        return authService.inscrire(
                "ami-" + UUID.randomUUID() + "@test.fr",
                "pseudo-" + UUID.randomUUID(),
                "secret123");
    }

    @Test
    void envoyer_une_demande_la_rend_visible_chez_le_destinataire() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();

        RelationUtilisateur relation = amiService.envoyerDemande(a, b);

        assertEquals(StatutRelation.EN_ATTENTE, relation.getStatut());
        assertEquals(1, amiService.demandesRecues(b).size());
        assertTrue(amiService.amis(a).isEmpty());
    }

    @Test
    void accepter_une_demande_cree_l_amitie_des_deux_cotes() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        RelationUtilisateur relation = amiService.envoyerDemande(a, b);

        amiService.accepter(relation);

        assertEquals(StatutRelation.ACCEPTEE, amiService.statutEntre(a, b));
        assertEquals(1, amiService.amis(a).size());
        assertEquals(1, amiService.amis(b).size());
    }

    @Test
    void refuser_une_demande_ne_cree_pas_d_amitie() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        RelationUtilisateur relation = amiService.envoyerDemande(a, b);

        amiService.refuser(relation);

        assertEquals(StatutRelation.REJETEE, amiService.statutEntre(a, b));
        assertTrue(amiService.amis(a).isEmpty());
    }

    @Test
    void supprimer_un_ami_efface_la_relation() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        RelationUtilisateur relation = amiService.envoyerDemande(a, b);
        amiService.accepter(relation);

        amiService.supprimer(a, b);

        assertNull(amiService.statutEntre(a, b));
        assertTrue(amiService.amis(a).isEmpty());
    }

    @Test
    void bloquer_puis_debloquer_un_utilisateur() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();

        amiService.bloquer(a, b);

        assertEquals(StatutRelation.BLOQUEE, amiService.statutEntre(a, b));
        assertEquals(1, amiService.bloques(a).size());

        amiService.debloquer(a, b);

        assertNull(amiService.statutEntre(a, b));
        assertTrue(amiService.bloques(a).isEmpty());
    }

    @Test
    void bloquer_un_ami_retire_l_amitie() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        RelationUtilisateur relation = amiService.envoyerDemande(a, b);
        amiService.accepter(relation);

        amiService.bloquer(a, b);

        assertEquals(StatutRelation.BLOQUEE, amiService.statutEntre(a, b));
        assertTrue(amiService.amis(a).isEmpty());
    }

    @Test
    void deux_demandes_identiques_sont_refusees() {
        Utilisateur a = utilisateur();
        Utilisateur b = utilisateur();
        amiService.envoyerDemande(a, b);

        assertThrows(IllegalArgumentException.class, () -> amiService.envoyerDemande(a, b));
    }

    @Test
    void on_ne_peut_pas_s_ajouter_soi_meme() {
        Utilisateur a = utilisateur();

        assertThrows(IllegalArgumentException.class, () -> amiService.envoyerDemande(a, a));
    }
}
