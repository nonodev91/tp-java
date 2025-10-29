package models;

import config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe Auteur (fusion Modèle + DAO pour débutants)
 * --------------------------------------------------
 * Cette classe représente un auteur (objet métier)
 * ET contient aussi les méthodes d'accès à la base de données (CRUD).
 *
 * ⚠️ Remarque pédagogique :
 * - Ici, pour simplifier, on mélange Modèle (objet Java) et DAO (requêtes SQL).
 * - Dans une vraie application MVC, les DAO seraient séparés.
 */
public class Auteur {

    // ================== ATTRIBUTS (données du Modèle) ==================
    private int idAuteur;        // Identifiant unique de l'auteur (clé primaire en BDD)
    private String prenomAuteur; // Prénom de l'auteur
    private String nomAuteur;    // Nom de l'auteur

    // ----- Getters et Setters -----
    public int getIdAuteur() { return idAuteur; }
    public void setIdAuteur(int idAuteur) { this.idAuteur = idAuteur; }

    public String getPrenomAuteur() { return prenomAuteur; }
    public void setPrenomAuteur(String prenomAuteur) { this.prenomAuteur = prenomAuteur; }

    public String getNomAuteur() { return nomAuteur; }
    public void setNomAuteur(String nomAuteur) { this.nomAuteur = nomAuteur; }

    // ================== MÉTHODES UTILITAIRES ==================

    /**
     * Cette méthode sera utilisée par Swing (ex: JComboBox)
     * pour afficher le prénom + nom de l’auteur dans la liste déroulante.
     */
    @Override
    public String toString() {
        return prenomAuteur + " " + nomAuteur;
    }

    /**
     * Deux auteurs sont considérés comme égaux
     * s’ils ont le même identifiant en BDD.
     * Utile pour comparer des objets (ex: pré-sélection dans JComboBox).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Auteur)) return false;
        Auteur auteur = (Auteur) o;
        return this.idAuteur == auteur.idAuteur;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAuteur);
    }

    // ====================================================================
    // ================== PARTIE "DAO" (Accès base de données) =============
    // ====================================================================

    /**
     * Lire tous les auteurs
     * ----------------------
     * - Exécute une requête SQL : SELECT * FROM auteur
     * - Chaque ligne du résultat est convertie en objet Auteur
     * - On renvoie une liste d’auteurs
     */
    public static List<Auteur> getAllAuteurs() {
        List<Auteur> auteurs = new ArrayList<>();
        String sql = "SELECT * FROM auteur"; // requête SQL

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql); // Préparation (plus sûr que Statement)
             ResultSet rs = ps.executeQuery()) { // Exécution -> retour d’un ResultSet (curseur)

            while (rs.next()) { // Parcours ligne par ligne
                Auteur a = new Auteur();
                a.setIdAuteur(rs.getInt("id_auteur"));          // récupération colonne id_auteur
                a.setPrenomAuteur(rs.getString("prenom_auteur"));
                a.setNomAuteur(rs.getString("nom_auteur"));
                auteurs.add(a); // ajout à la liste
            }

        } catch (SQLException e) {
            System.err.println("Erreur getAllAuteurs : " + e.getMessage());
        }
        return auteurs;
    }

    /**
     * Vérifier si un auteur existe déjà (éviter doublons)
     * ---------------------------------------------------
     * - La requête contient des "?" : ce sont des paramètres à remplacer.
     * - Exemple : "prenom_auteur = ?" sera remplacé par le prénom fourni.
     */
    public static boolean exists(String prenom, String nom, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM auteur WHERE prenom_auteur = ? AND nom_auteur = ?";
        if (excludeId != null) sql += " AND id_auteur != ?"; // option pour exclure un auteur (modification)

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // ⚠️ Important : les paramètres sont numérotés à partir de 1 (pas 0 !)
            ps.setString(1, prenom); // premier "?"
            ps.setString(2, nom);    // deuxième "?"
            if (excludeId != null) {
                ps.setInt(3, excludeId); // troisième "?" si nécessaire
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0; // si COUNT(*) > 0 → existe déjà
            }

        } catch (SQLException e) {
            System.err.println("Erreur exists : " + e.getMessage());
        }
        return false;
    }

    /**
     * Ajouter un auteur
     * -----------------
     * - Vérifie d'abord si l'auteur existe déjà (via exists()).
     * - Si non, insère une nouvelle ligne en BDD.
     */
    public static boolean addAuteur(String prenom, String nom) {
        if (exists(prenom, nom, null)) return false; // évite les doublons

        String sql = "INSERT INTO auteur (prenom_auteur, nom_auteur) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prenom); // premier "?"
            ps.setString(2, nom);    // deuxième "?"

            return ps.executeUpdate() > 0; // executeUpdate = renvoie nb lignes modifiées

        } catch (SQLException e) {
            System.err.println("Erreur addAuteur : " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifier un auteur
     * ------------------
     * - Vérifie d’abord si ce prénom/nom existe déjà pour un autre auteur.
     * - Met ensuite à jour les colonnes avec UPDATE.
     */
    public static boolean updateAuteur(int idAuteur, String prenom, String nom) {
        if (exists(prenom, nom, idAuteur)) return false; // doublon → impossible

        String sql = "UPDATE auteur SET prenom_auteur = ?, nom_auteur = ? WHERE id_auteur = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, prenom); // premier "?"
            ps.setString(2, nom);    // deuxième "?"
            ps.setInt(3, idAuteur);  // troisième "?"

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur updateAuteur : " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprimer un auteur
     * -------------------
     * - Avant de supprimer, on vérifie si l'auteur est utilisé dans la table oeuvre.
     * - Si oui → suppression impossible.
     * - Sinon → suppression validée avec commit.
     */
    public static boolean deleteAuteur(int idAuteur) {
        String checkSql = "SELECT COUNT(*) FROM oeuvre WHERE id_auteur = ?";
        String deleteSql = "DELETE FROM auteur WHERE id_auteur = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // désactive commit automatique

            // Vérifie si l'auteur est lié à une œuvre
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, idAuteur);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback(); // annulation
                        return false;    // suppression interdite
                    }
                }
            }

            // Supprime si pas lié
            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                psDelete.setInt(1, idAuteur);
                if (psDelete.executeUpdate() > 0) {
                    conn.commit(); // valide la suppression
                    return true;
                } else {
                    conn.rollback(); // annule si rien supprimé
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur deleteAuteur : " + e.getMessage());
        }
        return false;
    }

    /**
     * Récupérer un auteur par son ID
     * ------------------------------
     * - Exécute SELECT avec un "?" remplacé par l’ID fourni.
     * - Si trouvé, retourne un objet Auteur.
     */
    public static Auteur getAuteurById(int idAuteur) {
        String sql = "SELECT * FROM auteur WHERE id_auteur = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAuteur); // paramètre remplacé

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Auteur a = new Auteur();
                    a.setIdAuteur(rs.getInt("id_auteur"));
                    a.setPrenomAuteur(rs.getString("prenom_auteur"));
                    a.setNomAuteur(rs.getString("nom_auteur"));
                    return a;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAuteurById : " + e.getMessage());
        }
        return null;
    }
}
