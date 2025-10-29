package models;

import config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Oeuvre (fusion Modèle + DAO pour débutants)
 * --------------------------------------------------
 * Représente une œuvre (ex: manga) ET contient les méthodes
 * pour accéder à la base de données (CRUD).
 *
 * Objectifs pédagogiques BTS SIO :
 * 1. Comprendre le rôle du Modèle dans MVC.
 * 2. Encapsuler les données via getters et setters.
 * 3. Apprendre JDBC avec PreparedStatement et paramètres positionnés.
 * 4. Vérifier les doublons avant insertion/modification.
 * 5. Gérer les transactions lors des suppressions.
 */
public class Oeuvre {

    // ================== ATTRIBUTS ==================
    private int idOeuvre; // Identifiant unique de l'œuvre (clé primaire)
    private String nomOeuvre; // Nom de l'œuvre
    private int idAuteur; // Identifiant de l'auteur associé

    // ================== GETTERS ET SETTERS ==================
    public int getIdOeuvre() {
        return idOeuvre;
    }

    public void setIdOeuvre(int idOeuvre) {
        this.idOeuvre = idOeuvre;
    }

    public String getNomOeuvre() {
        return nomOeuvre;
    }

    public void setNomOeuvre(String nomOeuvre) {
        this.nomOeuvre = nomOeuvre;
    }

    public int getIdAuteur() {
        return idAuteur;
    }

    public void setIdAuteur(int idAuteur) {
        this.idAuteur = idAuteur;
    }

    // ====================================================================
    // ================== PARTIE "DAO" (Accès base de données) =============
    // ====================================================================

    /**
     * Vérifie si une œuvre existe déjà (titre + auteur)
     * -----------------------------------------------
     * - Utilisation de PreparedStatement avec "?" → paramètre positionné
     * (sécurise contre injections SQL et facilite maintenance)
     * - excludeId permet d'exclure une œuvre existante lors d'une modification
     */
    private static boolean oeuvreExists(String nomOeuvre, int idAuteur, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM oeuvre WHERE nom_oeuvre = ? AND id_auteur = ?";
        if (excludeId != null)
            sql += " AND id_oeuvre != ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // ⚠️ Les paramètres "?" sont numérotés à partir de 1
            ps.setString(1, nomOeuvre);
            ps.setInt(2, idAuteur);
            if (excludeId != null)
                ps.setInt(3, excludeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0; // COUNT(*) > 0 → existe
            }

        } catch (SQLException e) {
            System.err.println("Erreur oeuvreExists : " + e.getMessage());
        }
        return false;
    }

    /**
     * Lire toutes les œuvres
     * -----------------------
     * - INNER JOIN avec auteur pour récupérer nom et prénom
     * - Chaque ligne du ResultSet devient un objet Oeuvre
     */
    public static List<Oeuvre> getAllOeuvres() {
        List<Oeuvre> oeuvres = new ArrayList<>();
        String sql = "SELECT o.id_oeuvre, o.nom_oeuvre, o.id_auteur, " +
                "a.prenom_auteur, a.nom_auteur " +
                "FROM oeuvre o INNER JOIN auteur a ON o.id_auteur = a.id_auteur";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Oeuvre o = new Oeuvre();
                o.setIdOeuvre(rs.getInt("id_oeuvre"));
                o.setNomOeuvre(rs.getString("nom_oeuvre"));
                o.setIdAuteur(rs.getInt("id_auteur"));
                oeuvres.add(o);
            }

        } catch (SQLException e) {
            System.err.println("Erreur getAllOeuvres : " + e.getMessage());
        }
        return oeuvres;
    }

    /**
     * Récupérer une œuvre par son ID
     */
    public static Oeuvre getOeuvreById(int idOeuvre) {
        String sql = "SELECT o.id_oeuvre, o.nom_oeuvre, o.id_auteur, " +
                "a.prenom_auteur, a.nom_auteur " +
                "FROM oeuvre o INNER JOIN auteur a ON o.id_auteur = a.id_auteur " +
                "WHERE o.id_oeuvre = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idOeuvre); // paramètre "?" remplacé par l'ID

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Oeuvre o = new Oeuvre();
                    o.setIdOeuvre(rs.getInt("id_oeuvre"));
                    o.setNomOeuvre(rs.getString("nom_oeuvre"));
                    o.setIdAuteur(rs.getInt("id_auteur"));
                    return o;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur getOeuvreById : " + e.getMessage());
        }
        return null;
    }

    /**
     * Ajouter une nouvelle œuvre
     * ---------------------------
     * - Vérifie si l'œuvre existe déjà (évite doublon)
     * - INSERT avec paramètres positionnés
     */
    public static boolean addOeuvre(String nomOeuvre, int idAuteur) {
        if (oeuvreExists(nomOeuvre, idAuteur, null))
            return false;

        String sql = "INSERT INTO oeuvre (nom_oeuvre, id_auteur) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nomOeuvre); // premier "?"
            ps.setInt(2, idAuteur); // deuxième "?"
            return ps.executeUpdate() > 0; // executeUpdate → nombre de lignes insérées

        } catch (SQLException e) {
            System.err.println("Erreur addOeuvre : " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifier une œuvre existante
     * -----------------------------
     * - Vérifie doublon avant UPDATE
     * - UPDATE avec paramètres positionnés
     */

    // --------- CODER ICI ---------
    public static boolean updateOeuvre(int idOeuvre ,int idAuteur, String nomOeuvre) {
    if (oeuvreExists(nomOeuvre, idAuteur, idOeuvre)) return false; 
    String sql = "UPDATE oeuvre SET nom_oeuvre = ?, id_auteur = ? WHERE id_oeuvre = ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nomOeuvre);
            ps.setInt(2, idAuteur);
            ps.setInt(3, idOeuvre);


            return ps.executeUpdate() > 0;

         } catch (SQLException e){
            System.err.println("Erreur updateOeuvre : " + e.getMessage());
            return false;
         }
     }
    // Ligne 183 : Création des méthodes pour modifier une oeuvre
    // Ces méthodes permettront de mettre à jour les informations d'une oeuvre
    // dans la base de données ou dans le modèle.
    //
    // Remarques pédagogiques BTS SIO :
    // 1. Comprendre la création et l'utilisation de méthodes dans un modèle.
    // 2. Respecter le principe MVC : le modèle contient la logique métier,
    // tandis que la vue (views/OeuvreView.java) gère les formulaires et boutons.
    // 3. Les appels à ces méthodes seront déclenchés par les boutons existants
    // dans l'interface utilisateur.

    /**
     * Supprimer une œuvre
     * -------------------
     * - Utilisation de transaction (commit/rollback)
     * - Si suppression réussie → commit, sinon rollback
     */

    // --------- CODER ICI ---------
        public static boolean deleteOeuvre(int idOeuvre){

        String deleteSql = "DELETE FROM oeuvre WHERE id_oeuvre = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);


            try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)){
                psDelete.setInt(1, idOeuvre);
                if (psDelete.executeUpdate()> 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                }
            }
        } catch (SQLException e){
            System.err.println("Erreur deleteOeuvre :" + e.getMessage());
        }
        return false;
    }

    // Ligne 202 : Création des méthodes pour modifier une œuvre
    // Ces méthodes permettront de mettre à jour les informations d'une œuvre
    // dans la base de données tout en respectant le modèle MVC.
    //
    // Points pédagogiques BTS SIO :
    // 1. Le modèle (models/Oeuvre.java) contient la logique métier et la gestion
    // des transactions, tandis que la vue (views/OeuvreView.java) gère les boutons
    // et formulaires, et le contrôleur (controller/OeuvreController.java) fait les
    // appels.
    // 2. Sécurité : utiliser PreparedStatement pour se protéger contre les
    // injections SQL.
    // 3. Gestion des transactions JDBC :
    // - conn.setAutoCommit(false) : désactive le commit automatique (équivalent de
    // BEGIN TRANSACTION)
    // - conn.commit() : valide la transaction
    // - conn.rollback() : annule la transaction en cas d’erreur
    // 4. Cette pratique garantit l’intégrité des données lors de modifications
    // multiples.

}
