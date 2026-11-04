package views;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.Vehicule;

public class Menu {
    public static List<Vehicule> vehicules = new ArrayList<>();

    public static void demarrer() {
        Scanner scanner = new Scanner(System.in);
        int choix = 0;

        while (choix != 8) {
            System.out.println("\n=== GESTION DES VEHICULES ===");
            System.out.println("1. Afficher les vehicules");
            System.out.println("2. Afficher les vehicules disponibles");
            System.out.println("3. Rechercher par marque");
            System.out.println("4. Louer un vehicule");
            System.out.println("5. Retourner un vehicule");
            System.out.println("6. Afficher le prix de location");
            System.out.println("7. Filtrer par budget maximal");
            System.out.println("8. Quitter");
            System.out.print("Votre choix : ");

            choix = scanner.nextInt();
            scanner.nextLine();

            if (choix == 1) {
                afficherVehicules();
            } else if (choix == 2) {
                afficherVehiculesDisponibles();
            } else if (choix == 3) {
                rechercherMarque(scanner);
            } else if (choix == 4) {
                louerVehicule(scanner);
            } else if (choix == 5) {
                retournerVehicule(scanner);
            } else if (choix == 6) {
                afficherPrixLocation(scanner);
            } else if (choix == 7) {
                afficherBudgetMax(scanner);
            } else if (choix == 8) {
                System.out.println("Au revoir !");
            } else {
                System.out.println("Option inconnue.");
            }
        }

        scanner.close();
    }

    private static void afficherVehicules() {
        for (Vehicule v : vehicules) {
            v.decrire();
        }
    }

    private static void afficherVehiculesDisponibles() {
        boolean trouve = false;

        for (Vehicule v : vehicules) {
            if (v.isDisponible()) {
                v.decrire();
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("Aucun vehicule disponible.");
        }
    }

    private static void rechercherMarque(Scanner scanner) {
        boolean trouve = false;
        System.out.print("Marque recherchee : ");
        String marqueRecherchee = scanner.nextLine();

        for (Vehicule v : vehicules) {
            if (v.getMarque().equalsIgnoreCase(marqueRecherchee)) {
                v.decrire();
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("Aucun vehicule avec cette marque.");
        }
    }

    private static void louerVehicule(Scanner scanner) {
        System.out.print("Immatriculation du vehicule a louer : ");
        String immatriculation = scanner.nextLine();

        Vehicule v = trouverVehicule(immatriculation);
        if (v == null) {
            System.out.println("Aucun vehicule avec cette immatriculation.");
        } else {
            v.louer();
        }
    }

    private static void retournerVehicule(Scanner scanner) {
        System.out.print("Immatriculation du vehicule a retourner : ");
        String immatriculation = scanner.nextLine();

        Vehicule v = trouverVehicule(immatriculation);
        if (v == null) {
            System.out.println("Aucun vehicule avec cette immatriculation.");
        } else {
            v.rendre();
        }
    }

    private static Vehicule trouverVehicule(String immatriculation) {
        for (Vehicule v : vehicules) {
            if (v.getImmatriculation().equalsIgnoreCase(immatriculation)) {
                return v;
            }
        }
        return null;
    }

    private static void afficherPrixLocation(Scanner scanner) {
        boolean trouve = false;
        System.out.print("Nombre de jours : ");
        int jours = scanner.nextInt();
        scanner.nextLine();

        for (Vehicule v : vehicules) {
            if (v.isDisponible()) {
                double prix = v.calculerPrixLocation(jours);
                System.out.println(v.getImmatriculation() + " : " + prix + " euros pour " + jours + " jour(s)");
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("Aucun vehicule disponible.");
        }
    }

    private static void afficherBudgetMax(Scanner scanner) {
        boolean trouve = false;
        System.out.print("Budget journalier maximum : ");
        int budget = scanner.nextInt();
        scanner.nextLine();

        for (Vehicule v : vehicules) {
            if (v.getTarifJournalier() <= budget) {
                v.decrire();
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("Aucun vehicule avec un tarif dans ce budget.");
        }
    }
}