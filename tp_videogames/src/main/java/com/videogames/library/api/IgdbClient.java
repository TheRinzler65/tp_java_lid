package com.videogames.library.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.videogames.library.model.ModeJeu;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class IgdbClient {

    private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String GAMES_URL = "https://api.igdb.com/v4/games";

    private final String clientId;
    private final String clientSecret;
    private final HttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    private String accessToken;
    private Instant expiration = Instant.EPOCH;

    public IgdbClient() {
        this(identifiant(), secret());
    }

    public IgdbClient(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = HttpClient.newHttpClient();
    }

    private static String identifiant() {
        String valeur = System.getenv("IGDB_CLIENT_ID");
        return valeur != null ? valeur : System.getProperty("IGDB_CLIENT_ID");
    }

    private static String secret() {
        String valeur = System.getenv("IGDB_CLIENT_SECRET");
        return valeur != null ? valeur : System.getProperty("IGDB_CLIENT_SECRET");
    }

    public boolean estConfigure() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    public List<JeuIgdb> rechercher(String texte, int limite)
            throws IOException, InterruptedException {
        if (!estConfigure()) {
            throw new IllegalStateException(
                    "IGDB n'est pas configuré : définissez IGDB_CLIENT_ID et IGDB_CLIENT_SECRET.");
        }
        String recherche = texte == null ? "" : texte.replace("\"", "").trim();
        int taille = Math.max(1, Math.min(limite, 50));

        String corps = "search \"" + recherche + "\";"
                + " fields name,summary,first_release_date,cover.url,"
                + "genres.name,platforms.name,game_modes.id;"
                + " limit " + taille + ";";

        HttpRequest requete = HttpRequest.newBuilder(URI.create(GAMES_URL))
                .header("Client-ID", clientId)
                .header("Authorization", "Bearer " + jeton())
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(corps))
                .build();

        HttpResponse<String> reponse = httpClient.send(requete, HttpResponse.BodyHandlers.ofString());
        if (reponse.statusCode() != 200) {
            throw new IOException("IGDB a répondu " + reponse.statusCode() + " : " + reponse.body());
        }
        return parser(reponse.body());
    }

    private synchronized String jeton() throws IOException, InterruptedException {
        if (accessToken != null && Instant.now().isBefore(expiration)) {
            return accessToken;
        }
        String corps = "client_id=" + encoder(clientId)
                + "&client_secret=" + encoder(clientSecret)
                + "&grant_type=client_credentials";

        HttpRequest requete = HttpRequest.newBuilder(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(corps))
                .build();

        HttpResponse<String> reponse = httpClient.send(requete, HttpResponse.BodyHandlers.ofString());
        if (reponse.statusCode() != 200) {
            throw new IOException("Authentification Twitch échouée (" + reponse.statusCode() + ")");
        }
        JsonNode json = mapper.readTree(reponse.body());
        accessToken = json.path("access_token").asText();
        long expiresIn = json.path("expires_in").asLong(3600);
        expiration = Instant.now().plusSeconds(Math.max(60, expiresIn - 60));
        return accessToken;
    }

    List<JeuIgdb> parser(String json) throws IOException {
        JsonNode racine = mapper.readTree(json);
        List<JeuIgdb> jeux = new ArrayList<>();
        if (!racine.isArray()) {
            return jeux;
        }
        for (JsonNode noeud : racine) {
            String titre = noeud.path("name").asText("");
            if (titre.isEmpty()) {
                continue;
            }
            String description = noeud.hasNonNull("summary") ? noeud.get("summary").asText() : null;
            LocalDate dateSortie = noeud.has("first_release_date")
                    ? Instant.ofEpochSecond(noeud.get("first_release_date").asLong())
                            .atZone(ZoneOffset.UTC).toLocalDate()
                    : null;

            String image = null;
            if (noeud.has("cover") && noeud.get("cover").hasNonNull("url")) {
                image = noeud.get("cover").get("url").asText();
                if (image.startsWith("//")) {
                    image = "https:" + image;
                }
            }

            List<String> genres = noms(noeud.get("genres"));
            List<String> plateformes = noms(noeud.get("platforms"));
            ModeJeu mode = mode(noeud.get("game_modes"));

            jeux.add(new JeuIgdb(noeud.path("id").asLong(), titre, description,
                    dateSortie, image, mode, genres, plateformes));
        }
        return jeux;
    }

    private static List<String> noms(JsonNode tableau) {
        List<String> noms = new ArrayList<>();
        if (tableau != null && tableau.isArray()) {
            for (JsonNode element : tableau) {
                if (element.hasNonNull("name")) {
                    noms.add(element.get("name").asText());
                }
            }
        }
        return noms;
    }

    static ModeJeu mode(JsonNode gameModes) {
        boolean solo = false;
        boolean multi = false;
        if (gameModes != null && gameModes.isArray()) {
            for (JsonNode element : gameModes) {
                long id = element.path("id").asLong(-1);
                if (id == 1) {
                    solo = true;
                } else if (id >= 2 && id <= 6) {
                    multi = true;
                }
            }
        }
        if (solo && multi) {
            return ModeJeu.SOLO_ET_ONLINE;
        }
        if (solo) {
            return ModeJeu.SOLO;
        }
        return ModeJeu.ONLINE;
    }

    private static String encoder(String valeur) {
        return URLEncoder.encode(valeur, StandardCharsets.UTF_8);
    }
}
