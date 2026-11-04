package com.videogames.library.service;

import com.videogames.library.dao.UtilisateurDAO;
import com.videogames.library.model.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Inscription et connexion des utilisateurs.
 * Les mots de passe sont stockés hachés (BCrypt), jamais en clair.
 */
public class AuthService {

    private final UtilisateurDAO utilisateurDAO;

    public AuthService() {
        this(new UtilisateurDAO());
    }

    public AuthService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    /**
     * Crée un compte et retourne l'utilisateur persisté.
     *
     * @throws IllegalArgumentException si un champ est invalide ou déjà utilisé
     */
    public Utilisateur inscrire(String email, String pseudo, String mdp) {
        String emailNettoye = email == null ? "" : email.trim().toLowerCase();
        String pseudoNettoye = pseudo == null ? "" : pseudo.trim();

        if (emailNettoye.isEmpty() || !emailNettoye.contains("@")) {
            throw new IllegalArgumentException("Adresse email invalide.");
        }
        if (pseudoNettoye.isEmpty()) {
            throw new IllegalArgumentException("Le pseudo ne peut pas etre vide.");
        }
        if (mdp == null || mdp.length() < 4) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caracteres.");
        }
        if (utilisateurDAO.trouverParEmail(emailNettoye) != null) {
            throw new IllegalArgumentException("Cette adresse email est deja utilisee.");
        }
        if (utilisateurDAO.trouverParPseudo(pseudoNettoye) != null) {
            throw new IllegalArgumentException("Ce pseudo est deja utilise.");
        }

        Utilisateur utilisateur = new Utilisateur(
                emailNettoye, BCrypt.hashpw(mdp, BCrypt.gensalt()), pseudoNettoye);
        utilisateurDAO.sauver(utilisateur);
        return utilisateur;
    }

    /**
     * Vérifie les identifiants.
     *
     * @return l'utilisateur connecté, ou {@code null} si les identifiants sont faux
     */
    public Utilisateur connecter(String email, String mdp) {
        if (email == null || mdp == null) {
            return null;
        }
        Utilisateur utilisateur = utilisateurDAO.trouverParEmail(email.trim().toLowerCase());
        if (utilisateur == null) {
            return null;
        }
        return BCrypt.checkpw(mdp, utilisateur.getMdp()) ? utilisateur : null;
    }
}
