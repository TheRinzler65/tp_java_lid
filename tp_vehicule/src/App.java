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

        Menu.demarrer();
    }
}