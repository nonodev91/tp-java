package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe DBConnection
 * ----------------------
 * Fournit une connexion unique à la base de données (Singleton).
 * Objectifs pédagogiques BTS SIO :
 * 1. Centraliser la gestion de la connexion
 * 2. Réutiliser la connexion dans les DAO
 * 3. Gérer les exceptions de connexion
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:8889/mangaworldoJAVA?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection connection = null;

    // Constructeur privé pour éviter l'instanciation
    private DBConnection() {}

    /**
     * Retourne la connexion unique à la base de données
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    /**
     * Ferme la connexion si nécessaire
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}
