package views;

import controllers.AuteurController;
import models.Auteur;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Vue pour afficher la liste des auteurs avec actions
 * ---------------------------------------------------
 * Objectifs pédagogiques BTS SIO :
 * - Comprendre la notion de "Vue" dans MVC (ici : interface graphique)
 * - Manipuler un tableau (JTable) pour afficher des données
 * - Ajouter, modifier et supprimer directement depuis l’interface
 * - Gérer les formulaires (fenêtres pop-up) pour la saisie utilisateur
 */
public class AuteurView extends JFrame {

    // Le contrôleur permet de dialoguer avec le modèle (la base de données)
    private AuteurController auteurController;
    private DefaultTableModel tableModel; // Modèle du tableau (contient les données affichées)
    private JTable table; // Tableau graphique affiché à l'écran

    /**
     * Constructeur de la Vue
     * ----------------------
     * On prépare ici la fenêtre principale (titre, taille, contenu...).
     */
    public AuteurView(AuteurController auteurController) {
        this.auteurController = auteurController;

        // Paramètres de la fenêtre principale
        setTitle("Liste des auteurs");
        setSize(700, 400);
        setLocationRelativeTo(null); // Centre la fenêtre sur l’écran
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ferme uniquement cette fenêtre

        // 1. Récupération des auteurs depuis le contrôleur
        List<Auteur> auteurs = auteurController.fetchAllAuteurs();

        // 2. Définition des colonnes du tableau
        String[] colonnes = {"ID", "Prénom", "Nom", "Modifier", "Supprimer"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            // On rend seulement les colonnes "Modifier" et "Supprimer" éditables
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 3;
            }
        };

        // 3. Remplissage du tableau avec les données
        for (Auteur a : auteurs) {
            tableModel.addRow(new Object[]{
                    a.getIdAuteur(),
                    a.getPrenomAuteur(),
                    a.getNomAuteur(),
                    "Modifier",   // bouton texte
                    "Supprimer"   // bouton texte
            });
        }

        // 4. Création du tableau graphique
        table = new JTable(tableModel);

        // 5. Ajout de boutons dans les colonnes "Modifier" et "Supprimer"
        table.getColumn("Modifier").setCellRenderer(new ButtonRenderer());
        table.getColumn("Modifier").setCellEditor(new ButtonEditor(new JCheckBox(), "Modifier"));

        table.getColumn("Supprimer").setCellRenderer(new ButtonRenderer());
        table.getColumn("Supprimer").setCellEditor(new ButtonEditor(new JCheckBox(), "Supprimer"));

        // 6. Ajout du tableau dans un "scroll" (ascenseur si beaucoup de lignes)
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 7. Bouton pour ajouter un auteur (en bas de la fenêtre)
        JButton addButton = new JButton("Ajouter un auteur");
        addButton.addActionListener(e -> showAuteurForm(null)); // null = on crée un nouvel auteur
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Méthode qui affiche un formulaire pour ajouter ou modifier un auteur.
     * @param auteur : objet Auteur existant (si modification), sinon null (ajout)
     */
    private void showAuteurForm(Auteur auteur) {
        // Création d'une fenêtre de type "dialogue"
        JDialog dialog = new JDialog(this, "Formulaire auteur", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(3, 2, 10, 10)); // grille de 3 lignes, 2 colonnes
        dialog.setLocationRelativeTo(this);

        // Champs du formulaire
        JLabel prenomLabel = new JLabel("Prénom :");
        JTextField prenomField = new JTextField();
        JLabel nomLabel = new JLabel("Nom :");
        JTextField nomField = new JTextField();

        // Si modification, on pré-remplit les champs
        if (auteur != null) {
            prenomField.setText(auteur.getPrenomAuteur());
            nomField.setText(auteur.getNomAuteur());
        }

        // Bouton d’enregistrement
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            String prenom = prenomField.getText().trim();
            String nom = nomField.getText().trim();

            // Vérification que les champs ne sont pas vides
            if (prenom.isEmpty() || nom.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Prénom et nom requis !");
                return;
            }

            // Cas 1 : ajout d’un nouvel auteur
            if (auteur == null) {
                boolean success = auteurController.createAuteur(prenom, nom);
                if (success) JOptionPane.showMessageDialog(dialog, "Auteur ajouté !");
                else JOptionPane.showMessageDialog(dialog, "Erreur ou doublon !");
            }
            // Cas 2 : modification d’un auteur existant
            else {
                boolean success = auteurController.modifyAuteur(auteur.getIdAuteur(), prenom, nom);
                if (success) JOptionPane.showMessageDialog(dialog, "Auteur modifié !");
                else JOptionPane.showMessageDialog(dialog, "Erreur ou doublon !");
            }

            dialog.dispose(); // ferme le formulaire
            refreshTable();   // recharge le tableau
        });

        // Ajout des composants dans la fenêtre
        dialog.add(prenomLabel);
        dialog.add(prenomField);
        dialog.add(nomLabel);
        dialog.add(nomField);
        dialog.add(new JLabel()); // case vide pour l’esthétique
        dialog.add(saveButton);

        dialog.setVisible(true);
    }

    /**
     * Rafraîchit le tableau après une modification de la base
     * (ajout, suppression ou modification d’un auteur).
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // vide le tableau
        List<Auteur> auteurs = auteurController.fetchAllAuteurs();
        for (Auteur a : auteurs) {
            tableModel.addRow(new Object[]{
                    a.getIdAuteur(),
                    a.getPrenomAuteur(),
                    a.getNomAuteur(),
                    "Modifier",
                    "Supprimer"
            });
        }
    }

    // ---------------------------------------------------
    // Classes internes pour gérer les boutons dans le tableau
    // ---------------------------------------------------

    /**
     * Classe pour afficher un bouton dans une cellule du tableau.
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true); // pour bien afficher le bouton
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * Classe pour gérer le clic sur un bouton (Modifier ou Supprimer).
     */
    class ButtonEditor extends DefaultCellEditor {
        private String label;   // texte du bouton (Modifier / Supprimer)
        private JButton button;
        private boolean clicked;
        private int row; // ligne du tableau concernée

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            this.label = label;

            // Quand on clique, on déclenche l’action
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText(label);
            this.row = row;
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                int id = (int) table.getValueAt(row, 0); // Récupère l’ID de l’auteur sélectionné

                // Cas 1 : suppression
                if (label.equals("Supprimer")) {
                    int confirm = JOptionPane.showConfirmDialog(AuteurView.this,
                            "Supprimer cet auteur ?", "Confirmer", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = auteurController.removeAuteur(id);
                        if (success) JOptionPane.showMessageDialog(AuteurView.this, "Auteur supprimé !");
                        else JOptionPane.showMessageDialog(AuteurView.this, "Erreur ou auteur lié à des œuvres !");
                        refreshTable();
                    }
                }
                // Cas 2 : modification
                else if (label.equals("Modifier")) {
                    Auteur a = null;
                    for (Auteur auteur : auteurController.fetchAllAuteurs()) {
                        if (auteur.getIdAuteur() == id) {
                            a = auteur;
                            break;
                        }
                    }
                    if (a != null) showAuteurForm(a);
                }
            }
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    /**
     * Affiche la fenêtre principale de l’application.
     */
    public void showWindow() {
        setVisible(true);
    }
}
