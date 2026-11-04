import models.Vehicule;
import views.Menu;

public class App {
    public static void main(String[] args) {
        Menu.ajouterVehicule(new Vehicule("QC-322-EZ", "Fiat", 50, true));
        Menu.ajouterVehicule(new Vehicule("QB-324-EE", "Citroen", 60, false));
        Menu.ajouterVehicule(new Vehicule("QA-325-ZZ", "Peugeot", 70, true));
        Menu.ajouterVehicule(new Vehicule("AB-111-CD", "Renault", 45, true));
        Menu.ajouterVehicule(new Vehicule("CD-222-EF", "Renault", 55, false));
        Menu.ajouterVehicule(new Vehicule("EF-333-GH", "Peugeot", 80, true));

        // Tests des differentes situations
        testerVehicule(new Vehicule("TEST-001", "Test", 30, true));
        testerVehicule(new Vehicule("TEST-002", "Test", 100, false));

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
