import models.Vehicule;
import views.Menu;

public class App {
    public static void main(String[] args) {
        Menu.vehicules.add(new Vehicule("QC-322-EZ", "Fiat", 50, true));
        Menu.vehicules.add(new Vehicule("QB-324-EE", "Citroen", 60, false));
        Menu.vehicules.add(new Vehicule("QA-325-ZZ", "Peugeot", 70, true));
        Menu.vehicules.add(new Vehicule("AB-111-CD", "Renault", 45, true));
        Menu.vehicules.add(new Vehicule("CD-222-EF", "Renault", 55, false));
        Menu.vehicules.add(new Vehicule("EF-333-GH", "Peugeot", 80, true));

        Menu.demarrer();
    }
}