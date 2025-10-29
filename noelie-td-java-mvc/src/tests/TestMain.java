package tests;

import models.Auteur;
import models.Oeuvre;
import controllers.AuteurController;
import controllers.OeuvreController;
import views.MainWindow;

import java.util.List;

/**
 * Classe de tests
 * -----------------------------
 * Vérifie le fonctionnement CRUD et la GUI sans API REST.
 *
 * Objectifs pédagogiques BTS SIO :
 * 1. Comprendre le fonctionnement du DAO et des contrôleurs.
 * 2. Tester l'affichage via la vue graphique.
 * 3. Vérifier les méthodes CRUD : insert, update, delete.
 * 4. Vérifier les retours des opérations pour gérer les erreurs.
 */
public class TestMain {

    public static void main(String[] args) {

        // -----------------------------
        // 1. TEST CRUD AUTEUR
        // -----------------------------
        AuteurController auteurController = new AuteurController();
        List<Auteur> auteurs = auteurController.fetchAllAuteurs();

        System.out.println("Auteurs initiaux :");
        for (Auteur a : auteurs) {
            System.out.println(a.getIdAuteur() + " - " + a.getPrenomAuteur() + " " + a.getNomAuteur());
        }

        // Exemple d'ajout d'un auteur
        boolean ajoutAuteur = auteurController.createAuteur("Jean", "Dupont");
        if (!ajoutAuteur) {
            System.err.println("Erreur : impossible d'ajouter l'auteur Jean Dupont (doublon ou problème DB).");
        }

        // -----------------------------
        // 2. TEST CRUD OEUVRE
        // -----------------------------
        OeuvreController oeuvreController = new OeuvreController();
        List<Oeuvre> oeuvres = oeuvreController.fetchAllOeuvres();

        System.out.println("\nŒuvres initiales :");
        for (Oeuvre o : oeuvres) {
            System.out.println(o.getIdOeuvre() + " - " + o.getNomOeuvre() + " (Auteur ID : " + o.getIdAuteur() + ")");
        }

        // Exemple d'ajout d'une œuvre (si un auteur existe)
        if (!auteurs.isEmpty()) {
            int auteurId = auteurs.get(0).getIdAuteur(); // exemple : utiliser un auteur existant
            boolean ajoutOeuvre = oeuvreController.createOeuvre("Nouveau Manga", auteurId);
            if (!ajoutOeuvre) {
                System.err.println("Erreur : impossible d'ajouter l'œuvre Nouveau Manga (doublon ou problème DB).");
            }
        }

        // -----------------------------
        // 3. TEST GUI
        // -----------------------------
        try {
            // Passe les deux contrôleurs à la fenêtre principale pour le CRUD interactif
            MainWindow mainWindow = new MainWindow(auteurController, oeuvreController);
            mainWindow.showWindow();
        } catch (Exception e) {
            System.err.println("Erreur lors du lancement de la fenêtre principale : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
