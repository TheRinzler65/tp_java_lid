package models;

public class Vehicule {
    private String immatriculation;
    private String marque;
    private int tarifJournalier;
    private boolean disponible;

    public Vehicule(String immatriculation, String marque, int tarifJournalier, boolean disponible) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.tarifJournalier = tarifJournalier;
        this.disponible = disponible;
    }

    public double calculerPrixLocation(int nombreJours) {
        double total = this.tarifJournalier * nombreJours;
        if (nombreJours >= 7) {
            return total * 0.9;
        }
        return total;
    }

    public boolean louer() {
        if (!this.disponible) {
            System.out.println("Le vehicule " + immatriculation + " n'est pas disponible.");
            return false;
        }
        this.disponible = false;
        System.out.println("Le vehicule " + immatriculation + " a ete loue avec succes.");
        return true;
    }

    public void rendre() {
        this.disponible = true;
        System.out.println("Le vehicule " + immatriculation + " a ete restitue.");
    }

    public void decrire() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return "Immatriculation : " + immatriculation + " | Marque : " + marque
                + " | Tarif : " + tarifJournalier + " euros par jour"
                + " | Dispo : " + (disponible ? "Oui" : "Non");
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public int getTarifJournalier() {
        return tarifJournalier;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public void setTarifJournalier(int tarifJournalier) {
        if (tarifJournalier < 0) {
            throw new IllegalArgumentException(
                    "Le tarif journalier ne peut pas etre negatif : " + tarifJournalier);
        }
        this.tarifJournalier = tarifJournalier;
    }
}