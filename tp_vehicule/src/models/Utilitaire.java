package models;

public class Utilitaire extends Vehicule {
    private final double volumeChargement;

    public Utilitaire(String immatriculation, String marque, int tarifJournalier,
                      boolean disponible, double volumeChargement) {
        super(immatriculation, marque, tarifJournalier, disponible);
        if (volumeChargement <= 0) {
            throw new IllegalArgumentException(
                    "Le volume de chargement doit etre positif : " + volumeChargement);
        }
        this.volumeChargement = volumeChargement;
    }

    public double getVolumeChargement() {
        return volumeChargement;
    }

    @Override
    public void decrire() {
        super.decrire();
        System.out.println("Volume de chargement : " + volumeChargement + " m3");
    }
}
