package com.videogames.library.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/** Critères de filtrage d'un tirage aléatoire. */
public class FiltreTirage {

    private final Set<Long> genreIds = new HashSet<>();
    private final Set<Long> plateformeIds = new HashSet<>();
    private BigDecimal prixMax;

    public Set<Long> getGenreIds() {
        return genreIds;
    }

    public Set<Long> getPlateformeIds() {
        return plateformeIds;
    }

    public BigDecimal getPrixMax() {
        return prixMax;
    }

    public void setPrixMax(BigDecimal prixMax) {
        this.prixMax = prixMax;
    }

    public void ajouterGenre(Long genreId) {
        genreIds.add(genreId);
    }

    public void ajouterPlateforme(Long plateformeId) {
        plateformeIds.add(plateformeId);
    }
}
