package views;

import controllers.AuteurController;
import controllers.OeuvreController;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale avec image de fond
 * --------------------------------------
 * Objectifs pédagogiques BTS SIO :
 * 1. Comprendre la notion de fenêtre principale (point d’entrée visuel de l’application).
 * 2. Gérer la navigation vers d’autres vues via des boutons.
 * 3. Illustrer l’utilisation d’une image de fond dans une interface Swing.
 * 4. Introduire une gestion simple des erreurs lors de l’ouverture des vues.
 */
public class MainWindow extends JFrame {

    // Références vers les contrôleurs
    private AuteurController auteurController;
    private OeuvreController oeuvreController;

    /**
     * Constructeur de la fenêtre principale
     *
     * param auteurController Contrôleur pour gérer les auteurs
     * param oeuvreController Contrôleur pour gérer les œuvres
     */
    public MainWindow(AuteurController auteurController, OeuvreController oeuvreController) {
        this.auteurController = auteurController;
        this.oeuvreController = oeuvreController;

        // -----------------------------
        // 1. Paramètres de la fenêtre
        // -----------------------------
        setTitle("MangaWorldO - Accueil");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centre la fenêtre

        // -----------------------------
        // 2. Chargement de l’image de fond
        // -----------------------------
        ImageIcon backgroundIcon = new ImageIcon("img/books.jpg"); // L’image doit être dans /img
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout()); // Permet de placer des composants dessus

        // -----------------------------
        // 3. Titre principal
        // -----------------------------
        JLabel titre = new JLabel("Bienvenue dans MangaWorldO", JLabel.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 28));
        titre.setForeground(Color.WHITE); // Texte blanc pour contraster

        // -----------------------------
        // 4. Panneau contenant les boutons
        // -----------------------------
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Transparent pour voir l’image
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // --- Bouton "Gérer les auteurs" ---
        JButton auteursButton = new JButton("Gérer les auteurs");
        auteursButton.addActionListener(e -> {
            try {
                // Passage du contrôleur à la vue
                AuteurView auteurView = new AuteurView(auteurController);
                auteurView.showWindow();
            } catch (Exception ex) {
                // Gestion des erreurs (pédagogique : éviter crash)
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ouverture de la vue Auteur : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- Bouton "Gérer les œuvres" ---
        JButton oeuvresButton = new JButton("Gérer les œuvres");
        oeuvresButton.addActionListener(e -> {
            try {
                // Passage des deux contrôleurs (nécessaire pour la comboBox Auteur)
                OeuvreView oeuvreView = new OeuvreView(oeuvreController, auteurController);
                oeuvreView.showWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ouverture de la vue Œuvre : " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ajout des boutons au panel
        buttonPanel.add(auteursButton);
        buttonPanel.add(oeuvresButton);

        // -----------------------------
        // 5. Placement dans l’interface
        // -----------------------------
        backgroundLabel.add(titre, BorderLayout.CENTER); // Le titre au centre
        backgroundLabel.add(buttonPanel, BorderLayout.SOUTH); // Les boutons en bas
        setContentPane(backgroundLabel);
    }

    /**
     * Méthode pour afficher la fenêtre principale
     */
    public void showWindow() {
        setVisible(true);
    }
}
