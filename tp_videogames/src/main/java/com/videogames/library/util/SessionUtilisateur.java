package com.videogames.library.util;

import com.videogames.library.model.Utilisateur;

public final class SessionUtilisateur {

    private static Utilisateur courant;

    private SessionUtilisateur() {
    }

    public static Utilisateur get() {
        return courant;
    }

    public static void ouvrir(Utilisateur utilisateur) {
        courant = utilisateur;
    }

    public static void fermer() {
        courant = null;
    }

    public static Utilisateur exiger() {
        if (courant == null) {
            throw new IllegalStateException("Aucun utilisateur connecte.");
        }
        return courant;
    }
}
