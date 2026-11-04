package com.videogames.library.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Dotenv {

    private static Map<String, String> valeurs;

    private Dotenv() {
    }

    public static synchronized Map<String, String> charger() {
        if (valeurs != null) {
            return valeurs;
        }
        Map<String, String> lues = new LinkedHashMap<>();
        Path fichier = Path.of(".env");
        if (!Files.isRegularFile(fichier)) {
            valeurs = Collections.unmodifiableMap(lues);
            return valeurs;
        }
        try {
            for (String ligne : Files.readAllLines(fichier, StandardCharsets.UTF_8)) {
                String texte = ligne.trim();
                if (texte.isEmpty() || texte.startsWith("#")) {
                    continue;
                }
                int egal = texte.indexOf('=');
                if (egal <= 0) {
                    continue;
                }
                String cle = texte.substring(0, egal).trim();
                String valeur = texte.substring(egal + 1).trim();
                if (valeur.length() >= 2
                        && ((valeur.startsWith("\"") && valeur.endsWith("\""))
                                || (valeur.startsWith("'") && valeur.endsWith("'")))) {
                    valeur = valeur.substring(1, valeur.length() - 1);
                }
                lues.put(cle, valeur);
                if (System.getenv(cle) == null && System.getProperty(cle) == null) {
                    System.setProperty(cle, valeur);
                }
            }
        } catch (IOException e) {
            System.err.println("[Dotenv] Lecture impossible : " + e.getMessage());
        }
        valeurs = Collections.unmodifiableMap(lues);
        return valeurs;
    }

    public static String valeur(String cle, String defaut) {
        charger();
        String env = System.getenv(cle);
        if (env != null && !env.isBlank()) {
            return env;
        }
        String propriete = System.getProperty(cle);
        if (propriete != null && !propriete.isBlank()) {
            return propriete;
        }
        return defaut;
    }
}
