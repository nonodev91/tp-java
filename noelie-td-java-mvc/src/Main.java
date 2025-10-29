import controllers.AuteurController;
import controllers.OeuvreController;
import views.MainWindow;

/**
 * Classe Main
 * -------------
 * Point d'entrée principal de l'application MangaWorldO.
 *
 * Objectifs pédagogiques BTS SIO :
 * 1. Comprendre le rôle de la méthode main.
 * 2. Lancer l'application avec la fenêtre principale.
 * 3. Initier les contrôleurs pour la gestion des auteurs et des œuvres.
 */
public class Main {

    public static void main(String[] args) {

        // -----------------------------
        // 1. Initialisation des contrôleurs
        // -----------------------------
        AuteurController auteurController = new AuteurController();
        OeuvreController oeuvreController = new OeuvreController();

        // -----------------------------
        // 2. Création et affichage de la fenêtre principale
        // -----------------------------
        // Passe les contrôleurs à la vue pour permettre les opérations CRUD
        MainWindow mainWindow = new MainWindow(auteurController, oeuvreController);
        mainWindow.showWindow();
    }
}
