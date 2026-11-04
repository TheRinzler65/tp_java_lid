package models;

public class Voiture extends Vehicule {
    private final int nombrePortes;

    public Voiture(String immatriculation, String marque, int tarifJournalier,
                   boolean disponible, int nombrePortes) {
        super(immatriculation, marque, tarifJournalier, disponible);
        if (nombrePortes <= 0) {
            throw new IllegalArgumentException(
                    "Le nombre de portes doit etre positif : " + nombrePortes);
        }
        this.nombrePortes = nombrePortes;
    }

    public int getNombrePortes() {
        return nombrePortes;
    }

    @Override
    public void decrire() {
        super.decrire();
        System.out.println("Nombre de portes : " + nombrePortes);
    }
}
