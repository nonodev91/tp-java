

pa

ckage views;

import controllers.AuteurController;
import controllers.OeuvreController;
import models.Auteur;
import models.Oeuvre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;

import java.util.List;

/**
 * Vue pour afficher la liste des Å“uvres avec actions
 * --------------------------------------------------- Objectifs pÃ©dagogiques
 * BTS SIO : - Comprendre la notion de "Vue" dans le modÃ¨le MVC (ici : interface
 * graphique). - Manipuler un tableau (JTable) pour afficher des donnÃ©es issues
 * de la base. - Ajouter, modifier et supprimer des Å“uvres via des formulaires.
 * - Utiliser une liste dÃ©roulante (JComboBox) pour choisir un auteur.
 */
public class OeuvreView extends JFrame {

    // ContrÃ´leurs (lien entre la Vue et le ModÃ¨le / base de donnÃ©es)
    private OeuvreController oeuvreController;
    private AuteurController auteurController;

    // ModÃ¨le de donnÃ©es du tableau et tableau graphique
    private DefaultTableModel tableModel;
    private JTable table;

    /**
     * Constructeur de la Vue ---------------------- On prÃ©pare ici la fenÃªtre
     * principale (titre, taille, contenu...).
     */
    public OeuvreView(OeuvreController oeuvreController, AuteurController auteurController) {
        this.oeuvreController = oeuvreController;
        this.auteurController = auteurController;

        // ParamÃ¨tres de la fenÃªtre principale
        setTitle("Liste des Å“uvres");
        setSize(700, 400);
        setLocationRelativeTo(null); // Centre la fenÃªtre
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 1. RÃ©cupÃ©ration de toutes les Å“uvres depuis le contrÃ´leur
        List<Oeuvre> oeuvres = oeuvreController.fetchAllOeuvres();

        // 2. DÃ©finition des colonnes du tableau
        String[] colonnes = {"ID", "Nom de l'Å“uvre", "Auteur", "Modifier", "Supprimer"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // seules les colonnes "Modifier" et "Supprimer" sont interactives
                return column >= 3;
            }
        };

        // 3. Remplissage du tableau avec les Å“uvres existantes
        for (Oeuvre o : oeuvres) {
            Auteur auteur = auteurController.findAuteurById(o.getIdAuteur());
            tableModel.addRow(new Object[]{
                o.getIdOeuvre(),
                o.getNomOeuvre(),
                auteur != null ? auteur.toString() : "Inconnu", // si pas trouvÃ©
                "Modifier",
                "Supprimer"
            });
        }

        // 4. CrÃ©ation du tableau graphique
        table = new JTable(tableModel);

        // 5. Ajout de boutons dans les colonnes "Modifier" et "Supprimer"
        table.getColumn("Modifier").setCellRenderer(new ButtonRenderer());
        table.getColumn("Modifier").setCellEditor(new ButtonEditor(new JCheckBox(), "Modifier"));

        table.getColumn("Supprimer").setCellRenderer(new ButtonRenderer());
        table.getColumn("Supprimer").setCellEditor(new ButtonEditor(new JCheckBox(), "Supprimer"));

        // 6. Ajout du tableau avec une barre de dÃ©filement
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ------ CODER ICI ----
        // 7. Bouton dâ€™ajout dâ€™une nouvelle Å“uvre (en bas de la fenÃªtre)
        JButton addButton = new JButton("ajouter une oeuvre");
        addButton.addActionListener(e -> showOeuvreForm(null));
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Ligne 93 : Ajout d'un bouton "Ajouter Oeuvre" Ã  l'interface
    // Ce bouton permettra Ã  l'utilisateur d'ouvrir le formulaire
    // ou de dÃ©clencher l'action pour ajouter une nouvelle oeuvre.
    // Objectifs pÃ©dagogiques BTS SIO :
    // 1. Comprendre l'ajout de composants Swing Ã  une interface.
    // 2. Utiliser les ActionListener pour gÃ©rer les interactions.
    // 3. Respecter le modÃ¨le MVC en appelant le contrÃ´leur appropriÃ©.
    /**
     * Formulaire pour ajouter ou modifier une Å“uvre
     * ---------------------------------------------
     *
     * @param oeuvre : objet existant si modification, sinon null pour un ajout
     */
    private void showOeuvreForm(Oeuvre oeuvre) {
        JDialog dialog = new JDialog(this, "Formulaire Å“uvre", true);
        dialog.setSize(350, 200);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setLocationRelativeTo(this);

        // Champs du formulaire
        JLabel nomLabel = new JLabel("Nom de l'Å“uvre :");
        JTextField nomField = new JTextField();
        JLabel auteurLabel = new JLabel("Auteur :");
        JComboBox<Auteur> auteurCombo = new JComboBox<>();

        // Remplissage de la liste dÃ©roulante avec les auteurs disponibles
        List<Auteur> auteurs = auteurController.fetchAllAuteurs();
        for (Auteur a : auteurs) {
            auteurCombo.addItem(a);
        }

        // PrÃ©-remplissage si modification
        if (oeuvre != null) {
            nomField.setText(oeuvre.getNomOeuvre());
            Auteur selectedAuteur = auteurController.findAuteurById(oeuvre.getIdAuteur());
            auteurCombo.setSelectedItem(selectedAuteur);
        }

        // Bouton dâ€™enregistrement
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            String nom = nomField.getText().trim();
            Auteur auteurSelectionne = (Auteur) auteurCombo.getSelectedItem();

            // VÃ©rification des champs
            if (nom.isEmpty() || auteurSelectionne == null) {
                JOptionPane.showMessageDialog(dialog, "Nom et auteur requis !");
                return;
            }

            // Cas 1 : ajout
            if (oeuvre == null) {
                boolean success = oeuvreController.createOeuvre(nom, auteurSelectionne.getIdAuteur());
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Å’uvre ajoutÃ©e !");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur ou doublon !");
                }
            } // Cas 2 : modification
            else {
                boolean success = oeuvreController.modifyOeuvre(
                        oeuvre.getIdOeuvre(), nom, auteurSelectionne.getIdAuteur());
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Å’uvre modifiÃ©e !");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Erreur ou doublon !");
                }
            }

            dialog.dispose();
            refreshTable(); // met Ã  jour le tableau
        });

        // Ajout des composants dans la fenÃªtre
        dialog.add(nomLabel);
        dialog.add(nomField);
        dialog.add(auteurLabel);
        dialog.add(auteurCombo);
        dialog.add(new JLabel()); // espace vide
        dialog.add(saveButton);

        dialog.setVisible(true);
    }

    /**
     * RafraÃ®chit le tableau aprÃ¨s ajout / modification / suppression
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // vide le tableau
        List<Oeuvre> oeuvres = oeuvreController.fetchAllOeuvres();
        for (Oeuvre o : oeuvres) {
            Auteur auteur = auteurController.findAuteurById(o.getIdAuteur());
            tableModel.addRow(new Object[]{
                o.getIdOeuvre(),
                o.getNomOeuvre(),
                auteur != null ? auteur.toString() : "Inconnu",
                "Modifier",
                "Supprimer"
            });
        }
    }

    // ---------------------
    // Classes internes pour gÃ©rer les boutons du tableau
    // ---------------------
    /**
     * Permet dâ€™afficher un bouton dans une cellule du tableau.
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
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
     * GÃ¨re lâ€™action quand on clique sur un bouton (Modifier ou Supprimer).
     */
    class ButtonEditor extends DefaultCellEditor {

        private String label; // texte du bouton
        private JButton button;
        private boolean clicked;
        private int row; // ligne concernÃ©e

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            this.label = label;
            button.addActionListener(e -> fireEditingStopped()); // dÃ©clenche lâ€™action
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
                int id = (int) table.getValueAt(row, 0); // rÃ©cupÃ¨re lâ€™ID de lâ€™Å“uvre

                // Cas 1 : suppression
                if (label.equals("Supprimer")) {
                    int confirm = JOptionPane.showConfirmDialog(OeuvreView.this,
                            "Supprimer cette Å“uvre ?", "Confirmer", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = oeuvreController.removeOeuvre(id);
                        if (success) {
                            JOptionPane.showMessageDialog(OeuvreView.this, "Å’uvre supprimÃ©e !");
                        } else {
                            JOptionPane.showMessageDialog(OeuvreView.this, "Erreur ou Å“uvre liÃ©e !");
                        }
                        refreshTable();
                    }
                } // Cas 2 : modification
                else if (label.equals("Modifier")) {
                    Oeuvre o = null;
                    for (Oeuvre oeuvre : oeuvreController.fetchAllOeuvres()) {
                        if (oeuvre.getIdOeuvre() == id) {
                            o = oeuvre;
                            break;
                        }
                    }
                    if (o != null) {
                        showOeuvreForm(o);
                    }
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
     * Affiche la fenÃªtre principale de lâ€™application.
     */
    public void showWindow() {
        setVisible(true);
    }

}
