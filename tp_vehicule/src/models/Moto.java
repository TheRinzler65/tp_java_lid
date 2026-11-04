package models;

public class Moto extends Vehicule {
    private final int cylindree;

    public Moto(String immatriculation, String marque, int tarifJournalier,
                boolean disponible, int cylindree) {
        super(immatriculation, marque, tarifJournalier, disponible);
        if (cylindree <= 0) {
            throw new IllegalArgumentException(
                    "La cylindree doit etre positive : " + cylindree);
        }
        this.cylindree = cylindree;
    }

    public int getCylindree() {
        return cylindree;
    }

    @Override
    public void decrire() {
        super.decrire();
        System.out.println("Cylindree : " + cylindree + " cm3");
    }
}
