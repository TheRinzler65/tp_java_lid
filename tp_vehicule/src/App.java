import models.Moto;
import models.Utilitaire;
import models.Vehicule;
import models.Voiture;
import views.Menu;

public class App {
    public static void main(String[] args) {
        Menu.ajouterVehicule(new Vehicule("QC-322-EZ", "Fiat", 50, true));
        Menu.ajouterVehicule(new Vehicule("QB-324-EE", "Citroen", 60, false));
        Menu.ajouterVehicule(new Vehicule("QA-325-ZZ", "Peugeot", 70, true));
        Menu.ajouterVehicule(new Vehicule("AB-111-CD", "Renault", 45, true));
        Menu.ajouterVehicule(new Vehicule("CD-222-EF", "Renault", 55, false));
        Menu.ajouterVehicule(new Vehicule("EF-333-GH", "Peugeot", 80, true));

        // Deux voitures
        Voiture voiture1 = new Voiture("AA-111-BB", "Renault Clio", 55, true, 5);
        Voiture voiture2 = new Voiture("CC-222-DD", "Tesla Model 3", 120, true, 4);

        // Deux motos
        Moto moto1 = new Moto("EE-333-FF", "Yamaha MT-07", 40, true, 689);
        Moto moto2 = new Moto("GG-444-HH", "Harley-Davidson Sportster", 75, false, 1200);

        // Deux utilitaires
        Utilitaire utilitaire1 = new Utilitaire("II-555-JJ", "Ford Transit", 90, true, 12.5);
        Utilitaire utilitaire2 = new Utilitaire("KK-666-LL", "Mercedes Sprinter", 110, true, 18.0);

        System.out.println("\n=== DESCRIPTION DES NOUVEAUX VEHICULES ===");
        voiture1.decrire();
        voiture2.decrire();
        moto1.decrire();
        moto2.decrire();
        utilitaire1.decrire();
        utilitaire2.decrire();

        // Tests des differentes situations
        testerVehicule(new Vehicule("TEST-001", "Test", 30, true));
        testerVehicule(new Vehicule("TEST-002", "Test", 100, false));

        // Verification des methodes heritees sur les sous-classes
        System.out.println("\n=== VERIFICATION DES METHODES HERITEES ===");
        testerVehicule(voiture1);
        testerVehicule(voiture2);
        testerVehicule(moto1);
        testerVehicule(moto2);
        testerVehicule(utilitaire1);
        testerVehicule(utilitaire2);

        Menu.demarrer();
    }

    private static void testerVehicule(Vehicule v) {
        System.out.println("\n=== Test du vehicule " + v.getImmatriculation() + " ===");
        v.decrire();

        System.out.println("Prix pour 3 jours : " + v.calculerPrixLocation(3));
        System.out.println("Prix pour 10 jours (remise de 10%) : " + v.calculerPrixLocation(10));

        System.out.println("Location : " + v.louer());
        System.out.println("Deuxieme location sans retour : " + v.louer());

        v.retourner();
        System.out.println("Disponible apres retour : " + v.isDisponible());

        try {
            v.setTarifJournalier(-1);
            System.out.println("ERREUR : le tarif negatif a ete accepte");
        } catch (IllegalArgumentException e) {
            System.out.println("Tarif negatif refuse : " + e.getMessage());
        }

        try {
            new Vehicule("TEST-003", "Test", -5, true);
            System.out.println("ERREUR : le constructeur a accepte un tarif negatif");
        } catch (IllegalArgumentException e) {
            System.out.println("Constructeur : tarif negatif refuse");
        }
    }
}
